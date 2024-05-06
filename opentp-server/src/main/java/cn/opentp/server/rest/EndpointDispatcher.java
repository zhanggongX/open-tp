package cn.opentp.server.rest;

import cn.opentp.core.util.JSONUtils;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.rest.endpoint.Endpoint;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointDispatcher {

    private static final Logger log = LoggerFactory.getLogger(EndpointDispatcher.class);

    private static void doDispatcher(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        String uri = httpRequest.uri();
        HttpMethod method = httpRequest.method();

        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }

        String[] split = uri.split("/");
        String endPoint = split[0];

        EndpointMapping endpointMapping = OpentpApp.instance().endpointMapping();
        Endpoint httpHandler = endpointMapping.mappingHandler(endPoint);

        if (HttpMethod.GET.equals(method)) {
            httpHandler.get(httpRequest, httpResponse);
        } else if (HttpMethod.POST.equals(method)) {
            httpHandler.post(httpRequest, httpResponse);
        } else if (HttpMethod.PUT.equals(method)) {
            httpHandler.put(httpRequest, httpResponse);
        } else if (HttpMethod.DELETE.equals(method)) {
            httpHandler.delete(httpRequest, httpResponse);
        } else {
            throw new IllegalArgumentException("不支持的请求方式");
        }
    }

    public static void dispatcher(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        try {
            EndpointDispatcher.doDispatcher(httpRequest, httpResponse);
        } catch (Exception e) {
            log.error("endpoint handler exception, ", e);
            // 统一拦截 endpoint 处理抛出的异常
            BaseRes<Void> fail = BaseRes.fail(-1, e.getMessage());
            String json = JSONUtils.toJson(fail);
            ByteBuf byteBuf = Unpooled.copiedBuffer(json, CharsetUtil.UTF_8);
            httpResponse.content().writeBytes(byteBuf);
            httpResponse.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, byteBuf.writerIndex());
        }
    }
}
