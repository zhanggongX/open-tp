package cn.opentp.server.network.receive.netty.handler;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.auth.License;
import cn.opentp.core.constant.OpentpCoreConstant;
import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageConstant;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.core.net.serializer.SerializerTypeEnum;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.application.ApplicationImpl;
import cn.opentp.server.domain.application.ApplicationRepository;
import cn.opentp.server.domain.connect.ConnectCommand;
import cn.opentp.server.domain.connect.ConnectCommandHandler;
import cn.opentp.server.infrastructure.auth.LicenseKeyFactory;
import cn.opentp.server.network.receive.ThreadPoolReceiveService;
import cn.opentp.server.network.receive.message.handler.MessageHandler;
import cn.opentp.server.service.domain.DomainCommandInvoker;
import com.google.inject.Injector;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class ReceiveServiceNettyHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(ReceiveServiceNettyHandler.class);
    private final OpentpApp opentpApp = OpentpApp.instance();
    private final Injector injector = opentpApp.injector();
    private final DomainCommandInvoker domainCommandInvoker = injector.getInstance(DomainCommandInvoker.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof OpentpMessage opentpMessage) {
//                ThreadPoolReceiveService threadPoolReportService = OpentpApp.instance().receiveService();
//
//                // 处理消息
//                threadPoolReportService.handle(ctx, opentpMessage);
                OpentpMessageTypeEnum opentpMessageTypeEnum = OpentpMessageTypeEnum.parse(opentpMessage.getMessageType());
                if (opentpMessageTypeEnum == null) {
                    log.warn("未知的消息类型，不处理！");
                    return;
                }

                switch (opentpMessageTypeEnum) {
                    case OpentpMessageTypeEnum.HEART_PING:
                        log.info("接收心跳信息： {} 应答: {}", opentpMessage.getData(), OpentpMessageConstant.HEARD_PONG);
                        break;
                    case OpentpMessageTypeEnum.THREAD_POOL_EXPORT:
                        break;
                    case OpentpMessageTypeEnum.THREAD_POOL_UPDATE:
                        break;
                    case OpentpMessageTypeEnum.AUTHENTICATION_REQ:
                        // 认证消息

                        ClientInfo clientInfo = (ClientInfo) opentpMessage.getData();
                        clientInfo.setServerInfo(OpentpApp.instance().selfInfo());

                        ConnectCommand connectCommand = new ConnectCommand();
                        ConnectCommandHandler connectCommandHandler = injector.getInstance(ConnectCommandHandler.class);
                        boolean connected = domainCommandInvoker.invoke(connectCommand, (q, c) -> connectCommandHandler.handle(q, connectCommand));

                        log.debug("有新认证到来，appKey: {}, appSecret: {}, host: {}, instance: {}", clientInfo.getAppKey(), clientInfo.getAppSecret(), clientInfo.getHost(), clientInfo.getInstance());
                        // todo 认证消息动态
                        if (clientInfo.getAppKey() == null) {
                            log.warn("新认证到来，未知的 appId : {}", clientInfo.getAppKey());
                            ctx.channel().close();
                            return;
                        }

                        ApplicationRepository applicationRepository = OpentpApp.instance().injector().getInstance(ApplicationRepository.class);
                        ApplicationImpl application = applicationRepository.queryByKey(clientInfo.getAppKey());

                        if (clientInfo.getAppSecret() == null || !clientInfo.getAppSecret().equals(application.getAppSecret())) {
                            log.warn("新认证到来, appId : {}, appSecret error ", clientInfo.getAppKey());
                            ctx.channel().close();
                            return;
                        }

                        String newLicenseKey = LicenseKeyFactory.get();
                        log.debug("新链接认证成功：返回 licenseKey : {}", newLicenseKey);

                        // 设置 licenseKey
                        ctx.channel().attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).set(newLicenseKey);

                        // 记录 appKey <-> 客户端信息
                        service.appKeyClientCache().putIfAbsent(clientInfo.getAppKey(), new ArrayList<>());
                        service.appKeyClientCache().get(clientInfo.getAppKey()).add(clientInfo);

                        // 记录 licenseKey <-> 客户端信息
                        service.licenseClientCache().put(newLicenseKey, clientInfo);
                        // 记录 客户端信息 <-> 网络连接
                        service.clientChannelCache().put(clientInfo, ctx.channel());

                        OpentpMessage opentpMessageRes = OpentpCoreConstant.OPENTP_MSG_PROTO.clone();
                        OpentpMessage
                                .builder()
                                .messageType(OpentpMessageTypeEnum.AUTHENTICATION_RES.getCode())
                                .serializerType(SerializerTypeEnum.Kryo.getType())
                                .data(new License(newLicenseKey))
                                .traceId(opentpMessage.getTraceId())
                                .buildTo(opentpMessageRes);

                        // 返回 licenseKey
                        ctx.channel().writeAndFlush(opentpMessageRes);


                        break;
                    case OpentpMessageTypeEnum.AUTHENTICATION_RES:
                        break;
                    default:
                        log.warn("不支持的消息类型，不处理！");
                        break;
                }

                // // 消息处理器初始化
                //        MessageHandler.HANDLER_MAP.put(OpentpMessageTypeEnum.AUTHENTICATION_REQ, new AuthMessageHandler());
                //        MessageHandler.HANDLER_MAP.put(OpentpMessageTypeEnum.THREAD_POOL_EXPORT, new ReceiveMessageHandler());


//                messageHandler.handle(this, ctx, opentpMessage);
            } else {
                log.warn("未知消息类型，丢弃！");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.warn("心跳超时，关闭连接！");
                ThreadPoolReceiveService threadPoolReportService = OpentpApp.instance().receiveService();
                threadPoolReportService.clientClose(ctx.channel());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("channel 捕获异常 : ", cause);
    }
}
