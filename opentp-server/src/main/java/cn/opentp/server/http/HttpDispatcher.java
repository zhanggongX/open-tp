package cn.opentp.server.http;

import cn.opentp.core.tp.ThreadPoolWrapper;
import cn.opentp.core.util.JSONUtils;
import cn.opentp.server.http.handler.HttpHandler;
import cn.opentp.server.tp.Configuration;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpDispatcher {


    public static FullHttpResponse doDispatcher(FullHttpRequest req) {
        if("/favicon.ico".equals(req.uri())){
            boolean keepAlive = HttpUtil.isKeepAlive(req);
            FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            if (keepAlive) {
                if (!req.protocolVersion().isKeepAliveDefault()) {
                    response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                }
            } else {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            }
            return response;
        }


        String uri = req.uri();
        HttpMethod method = req.method();

        String[] split = uri.split("/");
        String endPoint = split[1];
        Map<String, HttpHandler> endPoints = Configuration.configuration().getEndPoints();
        HttpHandler httpHandler = endPoints.get(endPoint);

        BaseRes res = null;
        if (HttpMethod.GET.equals(method)) {
            res = httpHandler.doGet(req);
        }else if(HttpMethod.POST.equals(method)){
            res = httpHandler.doPost(req);
        }else if(HttpMethod.PUT.equals(method)){
            res = httpHandler.doPut(req);
        }else if(HttpMethod.DELETE.equals(method)){
            res = httpHandler.doDelete(req);
        }

        boolean keepAlive = HttpUtil.isKeepAlive(req);

        FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK);
        response.content().writeBytes(JSONUtils.toJsonString(res).getBytes(StandardCharsets.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        if (keepAlive) {
            if (!req.protocolVersion().isKeepAliveDefault()) {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
        } else {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }
        return response;
    }
}
