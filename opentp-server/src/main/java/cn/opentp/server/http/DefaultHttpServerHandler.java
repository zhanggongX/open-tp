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
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
        boolean continueExpected = HttpUtil.is100ContinueExpected(httpRequest);
        if (continueExpected) {
            // 100 continue expected 处理
            ctx.writeAndFlush(new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.CONTINUE));
        }

        boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);

        // 生成 httpResponse
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.OK);
        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        if (keepAlive) {
            if (!httpRequest.protocolVersion().isKeepAliveDefault()) {
                httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
        } else {
            httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }


        HttpDispatcher.doDispatcher(httpRequest, httpResponse);

        ChannelFuture channelFuture = ctx.writeAndFlush(httpResponse);
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
