package cn.opentp.server.report.handler;

import cn.opentp.core.auth.OpentpAuthentication;
import cn.opentp.core.auth.OpentpLicense;
import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageConstant;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.core.net.serializer.SerializerTypeEnum;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.server.auth.LicenseKeyFactory;
import cn.opentp.server.configuration.Configuration;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class ReportServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(ReportServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof OpentpMessage opentpMessage) {
                channelRead0(ctx, opentpMessage);
            } else {
                log.warn("未知消息类型，丢弃！");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 处理 OpentpMessage
     *
     * @param ctx           channel 上下文
     * @param opentpMessage 信息
     */
    private void channelRead0(ChannelHandlerContext ctx, OpentpMessage opentpMessage) {

        OpentpMessageTypeEnum opentpMessageTypeEnum = OpentpMessageTypeEnum.parse(opentpMessage.getMessageType());

        switch (Objects.requireNonNull(opentpMessageTypeEnum)) {
            case HEART_PING:
                log.info("接收心跳信息： {} 应答: {}", opentpMessage.getData(), OpentpMessageConstant.HEARD_PONG);
                break;
            case THREAD_POOL_EXPORT:
                String licenseKey = opentpMessage.getLicenseKey();
                log.info("接受到信息，认证码：{}", licenseKey);
                // todo licenseKey 校验

                List<?> threadPoolStates = (List<?>) opentpMessage.getData();
                for (Object obj : threadPoolStates) {
                    ThreadPoolState threadPoolState = (ThreadPoolState) obj;
                    Configuration configuration = Configuration.configuration();

                    configuration.threadPoolStateCache().putIfAbsent(threadPoolState.getThreadPoolName(), new ThreadPoolState());
                    ThreadPoolState configThreadPoolState = configuration.threadPoolStateCache().get(threadPoolState.getThreadPoolName());
                    configThreadPoolState.flushState(threadPoolState);

                    configuration.channelCache().put(threadPoolState.getThreadPoolName(), ctx.channel());
                    log.debug("上报线程池信息 : {}", configThreadPoolState.toString());
                }
                break;
            case AUTHENTICATION_REQ:
                OpentpAuthentication opentpAuthentication = (OpentpAuthentication) opentpMessage.getData();
                // todo 根据信息认证
                log.debug("有新认证到来，appKey: {}, appSecret: {}, host: {}, instance: {}", opentpAuthentication.getAppKey(), opentpAuthentication.getAppSecret(), opentpAuthentication.getHost(), opentpAuthentication.getInstance());
                Configuration configuration = Configuration.configuration();
                OpentpMessage opentpMessageRes = Configuration.OPENTP_MSG_PROTO.clone();
                OpentpMessage
                        .builder()
                        .messageType(OpentpMessageTypeEnum.AUTHENTICATION_RES.getCode())
                        .serializerType(SerializerTypeEnum.Kryo.getType())
                        .data(new OpentpLicense(LicenseKeyFactory.get()))
                        .traceId(opentpMessage.getTraceId())
                        .buildTo(opentpMessageRes);

                // 返回 licenseKey
                ctx.channel().writeAndFlush(opentpMessageRes);
                break;
            default:
                log.warn("未知的消息类型，不处理！");
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.warn("心跳超时，关闭连接！");
                // todo 处理缓存 channel
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("channel 捕获异常 : ", cause);
        // todo 处理缓存 channel
        ctx.close();
    }
}
