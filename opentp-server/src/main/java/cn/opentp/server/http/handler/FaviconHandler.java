package cn.opentp.server.http.handler;

import cn.opentp.core.tp.ThreadPoolWrapper;
import cn.opentp.server.http.BaseRes;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Map;

public class FaviconHandler extends AbstractHttpHandler implements HttpHandler {

    @Override
    public void doGet(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        BaseRes<Map<String, ThreadPoolWrapper>> res = BaseRes.success();
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
