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
                log.info("接收心跳信息： {}, 应答: {}", opentpMessage.getData(), OpentpMessageConstant.HEARD_PONG);
                break;
            case THREAD_POOL_EXPORT:
                ThreadPoolState threadPoolState = (ThreadPoolState) opentpMessage.getData();
                Configuration configuration = Configuration.configuration();

                configuration.theadPoolStateCache().putIfAbsent(threadPoolState.getThreadPoolName(), new ThreadPoolState());
                ThreadPoolState configThreadPoolState = configuration.theadPoolStateCache().get(threadPoolState.getThreadPoolName());
                configThreadPoolState.flushState(threadPoolState);

                configuration.channelCache().put(threadPoolState.getThreadPoolName(), ctx.channel());
                log.debug("线程池信息 : {}", configThreadPoolState.toString());
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
        log.error("channel catch ex : {}", cause.toString());
        // todo 处理缓存 channel
        ctx.close();
    }
}
