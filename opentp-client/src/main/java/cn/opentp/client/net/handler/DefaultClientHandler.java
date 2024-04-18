package cn.opentp.client.net.handler;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.core.tp.ThreadPoolContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;


public class DefaultClientHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(DefaultClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("in active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ThreadPoolContext tpw = (ThreadPoolContext) msg;

        Map<String, ThreadPoolContext> threadPoolContextCache = Configuration.configuration().threadPoolContextCache();
        ThreadPoolContext threadPoolWrapper = threadPoolContextCache.get(tpw.getThreadName());

        ThreadPoolExecutor target = threadPoolWrapper.getTarget();
        target.setCorePoolSize(tpw.getCoreSize());

        log.info("doUpdate");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
        // todo 尝试重新链接到服务器。
    }
}
