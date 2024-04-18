package cn.opentp.server.net.handler;

import cn.opentp.core.tp.ThreadPoolContext;
import cn.opentp.server.tp.Configuration;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(DefaultServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ThreadPoolContext tpw = (ThreadPoolContext) msg;
        Configuration configuration = Configuration.configuration();
        configuration.getTpCache().put(tpw.getThreadName(), tpw);
        configuration.getTpChannel().put(tpw.getThreadName(), ctx.channel());
        log.debug("thread info : {}", tpw.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("server occur exception:" + cause.getMessage());
        cause.printStackTrace();
        // 关闭发生异常的连接
        ctx.close();
    }
}
