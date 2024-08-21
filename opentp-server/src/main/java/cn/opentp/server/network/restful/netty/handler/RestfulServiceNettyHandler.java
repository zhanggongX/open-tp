package cn.opentp.server.network.restful.netty.handler;

import cn.opentp.server.OpentpApp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestfulServiceNettyHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Logger log = LoggerFactory.getLogger(RestfulServiceNettyHandler.class);

    private final OpentpApp opentpApp = OpentpApp.instance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
        opentpApp.restfulService().handle(ctx, httpRequest);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Rest Service Netty Handler 捕获异常：", cause);
    }
}
