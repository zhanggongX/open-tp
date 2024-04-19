package cn.opentp.client.net.handler;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.core.thread.pool.ThreadPoolContext;
import cn.opentp.core.thread.pool.ThreadPoolState;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;


public class OpentpClientHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(OpentpClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("in active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ThreadPoolState threadPoolState = (ThreadPoolState) msg;

        Map<String, ThreadPoolContext> threadPoolContextCache = Configuration.configuration().threadPoolContextCache();
        ThreadPoolContext threadPoolContext = threadPoolContextCache.get(threadPoolState.getThreadPoolName());

        threadPoolContext.flushTarget(threadPoolState);

        log.info("doUpdate");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
        // todo 尝试重新链接到服务器。
    }
}
