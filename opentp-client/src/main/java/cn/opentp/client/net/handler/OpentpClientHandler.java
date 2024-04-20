package cn.opentp.client.net.handler;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageConstant;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.core.net.serializer.SerializerTypeEnum;
import cn.opentp.core.thread.pool.ThreadPoolContext;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.core.util.MessageTraceIdUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;


public class OpentpClientHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(OpentpClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof OpentpMessage opentpMessage) {
                channelRead0(ctx, opentpMessage);
            } else {
                log.warn("unknown message, discard");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                OpentpMessage opentpMessage = Configuration.OPENTP_MSG_PROTO.clone();
                OpentpMessage
                        .builder()
                        .messageType(OpentpMessageTypeEnum.HEART_PING.getCode())
                        .serializerType(SerializerTypeEnum.Kryo.getType())
                        .data(null)
                        .traceId(MessageTraceIdUtil.traceId())
                        .buildTo(opentpMessage);
                ctx.channel().writeAndFlush(opentpMessage);
            }
        } else {
            super.userEventTriggered(ctx, evt);
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
            case HEART_PONG:
                break;
            case THREAD_POOL_UPDATE:
                ThreadPoolState threadPoolState = (ThreadPoolState) opentpMessage.getData();

                Map<String, ThreadPoolContext> threadPoolContextCache = Configuration.configuration().threadPoolContextCache();
                ThreadPoolContext threadPoolContext = threadPoolContextCache.get(threadPoolState.getThreadPoolName());

                threadPoolContext.flushTargetState(threadPoolState);

                log.info("doUpdate");
                break;
            default:
                log.warn("unknown opentp message type;");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("channel catch exception : {}", cause.toString());
        ctx.close();
        // todo 尝试重新链接到服务器。
    }
}