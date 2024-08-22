package cn.opentp.server.network.restful.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class RestHttpResponse {

    private final FullHttpResponse httpResponse;

    private final ChannelHandlerContext channelHandlerContext;

    private final Map<String, String> headers = new HashMap<>();

    private final Map<String, String> cookies = new HashMap<>();

    public RestHttpResponse(ChannelHandlerContext ctx) {
        this.channelHandlerContext = ctx;
        this.httpResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer("", CharsetUtil.UTF_8));
        // 只支持 JSON 格式
        String contentType = "application/json; charset=UTF-8";
        httpResponse.headers().set("Content-Type", contentType);
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return this.channelHandlerContext;
    }

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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setContent(String content) {
        httpResponse.content().writeBytes(Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
        httpResponse.headers().setInt("Content-Length", httpResponse.content().readableBytes());
    }
}
