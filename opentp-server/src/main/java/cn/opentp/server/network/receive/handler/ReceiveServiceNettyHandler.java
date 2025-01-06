package cn.opentp.server.network.receive.handler;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.auth.License;
import cn.opentp.core.constant.OpentpCoreConstant;
import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageConstant;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.core.net.serializer.SerializerTypeEnum;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.connection.ConnectCommand;
import cn.opentp.server.domain.connection.ConnectCommandHandler;
import cn.opentp.server.domain.threadpool.ThreadPoolReportCommand;
import cn.opentp.server.domain.threadpool.ThreadPoolReportCommandHandler;
import cn.opentp.server.infrastructure.auth.LicenseFactory;
import cn.opentp.server.network.receive.ThreadPoolReceiveService;
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
import java.util.List;

public class ReceiveServiceNettyHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(ReceiveServiceNettyHandler.class);
    private final OpentpApp opentpApp = OpentpApp.instance();
    private final Injector injector = opentpApp.injector();
    private final DomainCommandInvoker domainCommandInvoker = injector.getInstance(DomainCommandInvoker.class);
    private final LicenseFactory licenseFactory = injector.getInstance(LicenseFactory.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof OpentpMessage opentpMessage) {
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

                        String licenseKey = opentpMessage.getLicenseKey();
                        log.debug("接受到信息，认证码：{}", licenseKey);
                        String channelLicenseKey = ctx.channel().attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).get();
                        if (!licenseKey.equals(channelLicenseKey)) {
                            log.warn("licenseKey error");
                            ctx.channel().close();
                            return;
                        }

                        List<?> threadPoolStates = (List<?>) opentpMessage.getData();
                        List<ThreadPoolState> list = new ArrayList<>();
                        for (Object obj : threadPoolStates) {
                            ThreadPoolState threadPoolState = (ThreadPoolState) obj;
                            list.add(threadPoolState);
                        }
                        ThreadPoolReportCommand threadPoolReportCommand = new ThreadPoolReportCommand(list, ctx.channel());
                        ThreadPoolReportCommandHandler threadPoolReportCommandHandler = injector.getInstance(ThreadPoolReportCommandHandler.class);
                        domainCommandInvoker.invoke((q) -> threadPoolReportCommandHandler.handle(q, threadPoolReportCommand));

                        break;
                    case OpentpMessageTypeEnum.THREAD_POOL_UPDATE:
                        break;
                    case OpentpMessageTypeEnum.AUTHENTICATION_REQ:
                        // 认证消息

                        ClientInfo clientInfo = (ClientInfo) opentpMessage.getData();
//                        clientInfo.setServerInfo(OpentpApp.instance().selfInfo());

                        ConnectCommand connectCommand = new ConnectCommand(clientInfo.getHost(), clientInfo.getInstance(), clientInfo.getAppKey(), clientInfo.getAppSecret());
                        ConnectCommandHandler connectCommandHandler = injector.getInstance(ConnectCommandHandler.class);
                        try {
                            domainCommandInvoker.invoke((q) -> connectCommandHandler.handle(q, connectCommand));
                            String newLicenseKey = licenseFactory.get();
                            // 设置 licenseKey，后续鉴权使用。
                            ctx.channel().attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).set(newLicenseKey);

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
                        } catch (Exception e) {
                            log.error("新连接处理失败：", e);
                            ctx.channel().close();
                            return;
                        }
                        break;
                    case OpentpMessageTypeEnum.AUTHENTICATION_RES:
                        break;
                    default:
                        log.warn("不支持的消息类型，不处理！");
                        break;
                }
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

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ThreadPoolReceiveService threadPoolReportService = OpentpApp.instance().receiveService();
        threadPoolReportService.clientClose(ctx.channel());
    }
}
