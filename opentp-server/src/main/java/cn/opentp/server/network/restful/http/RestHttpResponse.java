package cn.opentp.server.network.restful.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class RestHttpResponse {

    private final FullHttpResponse httpResponse;

    private final ChannelHandlerContext channelHandlerContext;

    private final Map<String, String> headers = new HashMap<>();

    private final Map<String, String> cookies = new HashMap<>();

    public RestHttpResponse(ChannelHandlerContext ctx, FullHttpResponse httpResponse) {
        this.channelHandlerContext = ctx;
        this.httpResponse = httpResponse;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return this.channelHandlerContext;
    }

//    public Map<String, String> getHeaders() {
//        return this.headers;
//    }

    public Map<String, String> getCookies() {
        return this.cookies;
    }

    public FullHttpResponse httpResponse() {
        return httpResponse;
    }

    /**
     * 关闭Channel
     */
    public void closeChannel() {
        if (this.channelHandlerContext != null && this.channelHandlerContext.channel() != null) {
            this.channelHandlerContext.channel().close();
        }
    }

    public ChannelFuture writeAndFlush(HttpVersion httpVersion, HttpResponseStatus httpResponseStatus, ByteBuf byteBuf) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpResponse.headers().add(entry.getKey(), entry.getValue());
        }
        httpResponse.headers().setInt("Content-Length", httpResponse.content().readableBytes());

        return this.channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(httpVersion, httpResponseStatus, byteBuf));
    }

    /**
     * 输出响应
     *
     * @param status
     * @param body
     * @throws InterruptedException
     */
    public void writeAndFlush(HttpStatus status, String body) {
        HttpResponseStatus responstStatus = HttpResponseStatus.parseLine(String.valueOf(status.value()));
        FullHttpResponse response = null;
        if (body == null || body.trim().equals("")) {
            response = new DefaultFullHttpResponse(HTTP_1_1, responstStatus);
        } else {
            response = new DefaultFullHttpResponse(HTTP_1_1, responstStatus, Unpooled.copiedBuffer(body, CharsetUtil.UTF_8));
        }

        Set<Map.Entry<String, String>> entrySet = headers.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            response.headers().add(entry.getKey(), entry.getValue());
        }
        response.headers().setInt("Content-Length", response.content().readableBytes());
        channelHandlerContext.writeAndFlush(response);
    }


    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setContent(String content) {
        httpResponse.content().writeBytes(Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
        httpResponse.headers().setInt("Content-Length", httpResponse.content().readableBytes());
    }

    public void setHeaders(String header, String value) {
        httpResponse.headers().set(header, value);
    }
}
