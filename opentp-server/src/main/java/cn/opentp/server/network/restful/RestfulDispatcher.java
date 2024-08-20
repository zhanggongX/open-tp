package cn.opentp.server.network.restful;

import cn.opentp.core.util.JacksonUtil;
import cn.opentp.server.constant.OpentpServerConstant;
import cn.opentp.server.network.restful.http.RequestInfo;
import cn.opentp.server.network.restful.http.RestHttpRequest;
import cn.opentp.server.network.restful.http.RestHttpResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 *
 */
public class RestfulDispatcher {

    private static final Logger log = LoggerFactory.getLogger(RestfulDispatcher.class);

//    public static void dispatcher(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
//        try {
//            RestfulDispatcher.doDispatcher(httpRequest, httpResponse);
//        } catch (Exception e) {
//            log.error("endpoint handler exception, ", e);
//            retFail(httpResponse, e);
//        }
//    }

    private static void doDispatcher(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
//        String uri = httpRequest.uri();
//        HttpMethod method = httpRequest.method();
//
//        String endpoint = getEndpoint(uri);
//
//        EndpointMappings endpointMapping = OpentpApp.instance().restfulService().endpointMapping();
//        Endpoint httpHandler = endpointMapping.mappingHandler(endpoint);
//
//        if (HttpMethod.GET.equals(method)) {
//            httpHandler.get(httpRequest, httpResponse);
//        } else if (HttpMethod.POST.equals(method)) {
//            httpHandler.post(httpRequest, httpResponse);
//        } else if (HttpMethod.PUT.equals(method)) {
//            httpHandler.put(httpRequest, httpResponse);
//        } else if (HttpMethod.DELETE.equals(method)) {
//            httpHandler.delete(httpRequest, httpResponse);
//        } else {
//            throw new IllegalArgumentException("不支持的请求方式");
//        }
    }

    /**
     * 获得 endpoint
     *
     * @param uri 路径
     * @return endpoint
     */
    private static String getEndpoint(String uri) {
        String[] uris = uri.split(OpentpServerConstant.URI_SPLIT);
        return "".equals(uris[0]) ? uris[1] : uris[0];
    }

    /**
     * 统一拦截 endpoint 处理抛出的异常
     *
     * @param httpResponse 返回的信息
     * @param e            所有 Restful 请求的异常信息
     */
    private static void retFail(FullHttpResponse httpResponse, Exception e) {
        BaseRes<Void> fail = BaseRes.fail(-1, e.getMessage());
        String json = JacksonUtil.toJSONString(fail);
        ByteBuf byteBuf = Unpooled.copiedBuffer(json, CharsetUtil.UTF_8);
        httpResponse.content().writeBytes(byteBuf);
        httpResponse.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, byteBuf.writerIndex());
    }

    public void dispatcher(RestHttpRequest request, RestHttpResponse response) {
        ChannelFuture channelFuture = null;

        if (request.methodName().equalsIgnoreCase("OPTIONS")) {
            // 处理“预检”请求
            channelFuture = processOptionsRequest(request, response);
        }

        if (!request.methodName().equalsIgnoreCase("OPTIONS")) {

            if (!request.methodName().equalsIgnoreCase("GET")) {
                String contentType = request.getHeader("Content-Type");
                if (contentType != null) {
                    if (contentType.contains(";")) {
                        contentType = contentType.split(";")[0];
                    }
                    switch (contentType.toLowerCase()) {
                        case "application/json":
                        case "application/json;charset=utf-8":
                            request.setRequestBody();
                            break;
                        default:
                            throw new UnsupportedOperationException();
                    }
                }
            }

            channelFuture = new RequestHandler().handleRequest(request, response);
        }

        // 如果是“预检”请求，则处理后关闭连接。
        if (request.methodName().equalsIgnoreCase("OPTIONS")) {
            if (channelFuture != null) {
                channelFuture.addListener(ChannelFutureListener.CLOSE);
            }
            return;
        }
        if (!HttpUtil.isKeepAlive(request.httpRequest())) {
            assert channelFuture != null;
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 处理Options请求
     *
     * @param request
     * @param response
     * @return
     */
    private ChannelFuture processOptionsRequest(RestHttpRequest request, RestHttpResponse response) {
        String[] requestHeaders = request.headers().get("Access-Control-Request-Headers").split(",");
        for (String requestHeader : requestHeaders) {
            if (!requestHeader.isEmpty()) {
                if (!requestHeaderAllowed(requestHeader, response)) {
                    response.writeAndFlush(HTTP_1_1, HttpResponseStatus.NOT_FOUND, Unpooled.copiedBuffer("", CharsetUtil.UTF_8)).addListener(ChannelFutureListener.CLOSE);
//                    HttpContextHolder.getResponse().getChannelHandlerContext().writeAndFlush(optionsResponse).addListener(ChannelFutureListener.CLOSE);
                    return null;
                }
            }
        }

        return response.writeAndFlush(HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer("", CharsetUtil.UTF_8));
    }

    /**
     * 判断请求头是否被允许
     *
     * @param requestHeader 请求头
     * @param httpResponse  响应信息
     * @return 请求头是否被允许
     */
    private boolean requestHeaderAllowed(String requestHeader, RestHttpResponse httpResponse) {
        Map<String, String> responseHeaders = httpResponse.getHeaders();
        String allowedHeader = responseHeaders.get("Access-Control-Allow-Headers");
        if (allowedHeader != null && !allowedHeader.isEmpty()) {
            return Stream.of(allowedHeader.split(",")).anyMatch(head -> head.equalsIgnoreCase(requestHeader));
        }
        return false;
    }
}
