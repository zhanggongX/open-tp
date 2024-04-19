package cn.opentp.server.net.handler;

import cn.opentp.core.thread.pool.ThreadPoolContext;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.server.configuration.Configuration;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpentpHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(OpentpHandler.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ThreadPoolState threadPoolState = (ThreadPoolState) msg;

        Configuration configuration = Configuration.configuration();
        configuration.theadPoolStateCache().put(threadPoolState.getThreadPoolName(), threadPoolState);
        configuration.channelCache().put(threadPoolState.getThreadPoolName(), ctx.channel());

        log.info("thread info : {}", threadPoolState.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("server occur exception:" + cause.getMessage());
        cause.printStackTrace();
        // 关闭发生异常的连接
        ctx.close();
    }
}
