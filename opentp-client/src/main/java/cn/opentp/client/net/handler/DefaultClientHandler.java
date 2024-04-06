package cn.opentp.client.net.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultClientHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(DefaultClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("in active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object in) {

        log.info("read");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
