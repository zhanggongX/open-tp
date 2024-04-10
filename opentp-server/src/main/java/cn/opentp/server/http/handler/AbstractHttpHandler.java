package cn.opentp.server.http.handler;

import cn.opentp.core.tp.ThreadPoolWrapper;
import cn.opentp.core.util.JSONUtils;
import cn.opentp.server.http.BaseRes;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class AbstractHttpHandler {

    protected void updateHttpResponse(FullHttpResponse httpResponse, Object content) {
        httpResponse.content().writeBytes(JSONUtils.toJson(content).getBytes(StandardCharsets.UTF_8));
        httpResponse.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
    }
}
