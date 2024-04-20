package cn.opentp.server.net.handler;

import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageConstant;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.server.configuration.Configuration;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class OpentpHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(OpentpHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
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
                opentpMessage.setMessageType(OpentpMessageTypeEnum.HEART_PONG.getCode());
                opentpMessage.setData(OpentpMessageConstant.HEARD_PONG);
                ctx.channel().writeAndFlush(opentpMessage);
                break;
            case THREAD_POOL_EXPORT:
                ThreadPoolState threadPoolState = (ThreadPoolState) opentpMessage.getData();
                Configuration configuration = Configuration.configuration();

                configuration.theadPoolStateCache().putIfAbsent(threadPoolState.getThreadPoolName(), new ThreadPoolState());
                ThreadPoolState configThreadPoolState = configuration.theadPoolStateCache().get(threadPoolState.getThreadPoolName());
                configThreadPoolState.flushState(threadPoolState);

                configuration.channelCache().put(threadPoolState.getThreadPoolName(), ctx.channel());
                log.info("thread info : {}", configThreadPoolState.toString());
                break;
            default:
                log.warn("unknown opentp message type;");
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("heart beat exception, close this channel");
                // todo 处理缓存
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("channel catch ex : {}", cause.toString());
        // todo 处理缓存
        ctx.close();
    }
}
