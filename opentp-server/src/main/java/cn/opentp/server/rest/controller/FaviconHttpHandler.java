package cn.opentp.server.rest.controller;

import cn.opentp.core.thread.pool.ThreadPoolContext;
import cn.opentp.server.rest.BaseRes;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Map;

public class FaviconHttpHandler extends AbstractHttpHandler implements HttpHandler {

    @Override
    public void doGet(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        BaseRes<Map<String, ThreadPoolContext>> res = BaseRes.success();
        updateHttpResponse(httpResponse, res);
    }

    @Override
    public void doPost(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {

    }

    @Override
    public void doPut(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {

    }

    @Override
    public void doDelete(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {

    }
}
