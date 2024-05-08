package cn.opentp.client.net.handler;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.core.auth.License;
import cn.opentp.core.constant.OpentpCoreConstant;
import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageConstant;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.core.net.serializer.SerializerTypeEnum;
import cn.opentp.core.thread.pool.ThreadPoolContext;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.core.util.MessageTraceIdUtil;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;


public class OpentpClientHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(OpentpClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof OpentpMessage opentpMessage) {
                channelRead0(ctx, opentpMessage);
            } else {
                log.warn("未知消息，丢弃！");
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
            case THREAD_POOL_UPDATE:
                ThreadPoolState threadPoolState = (ThreadPoolState) opentpMessage.getData();
                log.debug("接收到线程池更新命令：{}", JSON.toJSONString(threadPoolState));

                Map<String, ThreadPoolContext> threadPoolContextCache = Configuration.configuration().threadPoolContextCache();
                ThreadPoolContext threadPoolContext = threadPoolContextCache.get(threadPoolState.getThreadPoolName());

                threadPoolContext.flushTargetState(threadPoolState);
                break;
            case AUTHENTICATION_RES:
                License opentpLicense = (License) opentpMessage.getData();
                Configuration configuration = Configuration.configuration();
                // 认证成功，设置 license
                configuration.threadPoolStateReportChannel().attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).set(opentpLicense.getLicenseKey());
                break;
            default:
                log.warn("未知的消息类型，不处理！");
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();

            if (state == IdleState.WRITER_IDLE) {
                OpentpMessage opentpMessage = OpentpCoreConstant.OPENTP_MSG_PROTO.clone();
                OpentpMessage
                        .builder()
                        .messageType(OpentpMessageTypeEnum.HEART_PING.getCode())
                        .serializerType(SerializerTypeEnum.Kryo.getType())
                        .data(OpentpMessageConstant.HEARD_PING)
                        .traceId(MessageTraceIdUtil.traceId())
                        .buildTo(opentpMessage);

                log.info("发送心跳信息");
                ctx.channel().writeAndFlush(opentpMessage);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("channel catch exception : ", cause);
        ctx.close();
    }
}
