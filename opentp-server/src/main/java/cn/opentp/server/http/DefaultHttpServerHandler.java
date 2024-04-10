package cn.opentp.server.http;

import cn.opentp.core.util.JSONUtils;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DefaultHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Logger log = LoggerFactory.getLogger(DefaultHttpServerHandler.class);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        boolean continueExpected = HttpUtil.is100ContinueExpected(req);
        if (continueExpected) {
            // 100 continue expected 处理
            ctx.writeAndFlush(new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.CONTINUE));
        }

        FullHttpResponse response = HttpDispatcher.doDispatcher(req);

        boolean keepAlive = HttpUtil.isKeepAlive(req);
        ChannelFuture channelFuture = ctx.writeAndFlush(response);
        if (!keepAlive) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("server occur exception:" + cause.getMessage());
        cause.printStackTrace();
        // 关闭发生异常的连接
        ctx.close();
    }
}
