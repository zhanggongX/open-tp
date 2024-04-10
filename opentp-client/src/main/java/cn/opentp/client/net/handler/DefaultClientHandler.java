package cn.opentp.client.net.handler;

import cn.opentp.client.context.OpentpContext;
import cn.opentp.core.tp.ThreadPoolWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;


public class DefaultClientHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(DefaultClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("in active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ThreadPoolWrapper tpw = (ThreadPoolWrapper) msg;

        ThreadPoolWrapper threadPoolWrapper = OpentpContext.get(tpw.getThreadName());

        ThreadPoolExecutor target = threadPoolWrapper.getTarget();
        target.setCorePoolSize(tpw.getCoreSize());

        log.info("doUpdate");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
