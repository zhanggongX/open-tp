package cn.opentp.server.rest.endpoint;

import cn.opentp.core.util.JSONUtils;
import cn.opentp.server.rest.BaseRes;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;

import java.nio.charset.StandardCharsets;

public abstract class AbstractEndpointAdapter<T> implements Endpoint {

    private void dealEndpointRes(FullHttpResponse httpResponse, BaseRes<?> res) {
        httpResponse.content().writeBytes(JSONUtils.toJson(res).getBytes(StandardCharsets.UTF_8));
        httpResponse.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
    }

    @Override
    public void get(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        BaseRes<T> res = doGet(httpRequest, httpResponse);
        dealEndpointRes(httpResponse, res);
    }

    @Override
    public void post(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        BaseRes<Void> res = doPost(httpRequest, httpResponse);
        dealEndpointRes(httpResponse, res);
    }

    @Override
    public void put(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        BaseRes<Void> res = doPut(httpRequest, httpResponse);
        dealEndpointRes(httpResponse, res);
    }

    @Override
    public void delete(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        BaseRes<Void> res = doDelete(httpRequest, httpResponse);
        dealEndpointRes(httpResponse, res);
    }

    public abstract BaseRes<T> doGet(FullHttpRequest httpRequest, FullHttpResponse httpResponse);

    public abstract BaseRes<Void> doPost(FullHttpRequest httpRequest, FullHttpResponse httpResponse);

    public abstract BaseRes<Void> doPut(FullHttpRequest httpRequest, FullHttpResponse httpResponse);

    public abstract BaseRes<Void> doDelete(FullHttpRequest httpRequest, FullHttpResponse httpResponse);
}
