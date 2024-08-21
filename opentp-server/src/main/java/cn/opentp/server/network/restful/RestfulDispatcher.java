package cn.opentp.server.network.restful;

import cn.opentp.core.util.JacksonUtil;
import cn.opentp.server.network.restful.convert.Converter;
import cn.opentp.server.network.restful.convert.ConverterFactory;
import cn.opentp.server.network.restful.dto.BaseRes;
import cn.opentp.server.network.restful.dto.BaseResCode;
import cn.opentp.server.network.restful.http.*;
import cn.opentp.server.network.restful.mapping.EndpointMapping;
import cn.opentp.server.network.restful.mapping.EndpointMappingParam;
import cn.opentp.server.network.restful.mapping.EndpointMappings;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * RESTFul 服务 dispatcher
 *
 * @author zg
 */
public class RestfulDispatcher {

    private static final Logger log = LoggerFactory.getLogger(RestfulDispatcher.class);

    public void doDispatch(RestHttpRequest request, RestHttpResponse response) {

        // 处理请求
        handleRequest(request, response);

        ChannelHandlerContext channelHandlerContext = response.getChannelHandlerContext();
        ChannelFuture channelFuture = channelHandlerContext.writeAndFlush(response.httpResponse());

        // 如果是“预检”请求，则处理后关闭连接。
        if (request.methodName().equalsIgnoreCase("OPTIONS") || !HttpUtil.isKeepAlive(request.httpRequest())) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 处理请求
     */
    public void handleRequest(RestHttpRequest request, RestHttpResponse response) {

        if (request.methodName().equalsIgnoreCase("OPTIONS")) {
            // 处理“预检”请求
            handleOptionsRequest(request, response);
            return;
        }

        // 校验请求返回 false
        if (!checkRequest(request, response)) {
            return;
        }

        // 查找匹配的Mapping
        EndpointMapping mapping = EndpointMappings.matchEndpointMapping(request);
        if (mapping == null) {
            // 全局异常处理
            response.httpResponse().setStatus(HttpResponseStatus.NOT_FOUND);
            response.httpResponse().content().writeBytes(Unpooled.copiedBuffer("接口不存在", CharsetUtil.UTF_8));
            return;
        }

        // 准备方法参数
        Object[] paramValues = new Object[mapping.getParams().size()];
        Class<?>[] paramTypes = new Class[mapping.getParams().size()];
        for (int i = 0; i < paramValues.length; i++) {
            EndpointMappingParam endpointMappingParam = mapping.getParams().get(i);
            Converter<?> converter = null;
            switch (endpointMappingParam.getType()) {
                case HTTP_REQUEST:
                    paramValues[i] = request.httpRequest();
                    break;
                case HTTP_RESPONSE:
                    paramValues[i] = response.httpResponse();
                    break;
                case REQUEST_BODY:
                    paramValues[i] = request.getRequestBody();
                    break;
                case REQUEST_PARAM:
                    paramValues[i] = request.getParam(endpointMappingParam.getName());
                    converter = ConverterFactory.create(endpointMappingParam.getDataType());
                    if (converter != null) {
                        paramValues[i] = converter.convert(paramValues[i]);
                    }
                    break;
                case REQUEST_HEADER:
                    paramValues[i] = request.getParam(endpointMappingParam.getName());
                    converter = ConverterFactory.create(endpointMappingParam.getDataType());
                    if (converter != null) {
                        paramValues[i] = converter.convert(request.getHeader(endpointMappingParam.getName()));
                    }
                    break;
                case PATH_VARIABLE:
                    paramValues[i] = this.getPathVariable(request.uri(), mapping.getRequestUrl(), endpointMappingParam.getName());
                    converter = ConverterFactory.create(endpointMappingParam.getDataType());
                    if (converter != null) {
                        paramValues[i] = converter.convert(paramValues[i]);
                    }
                    break;
//                case URL_ENCODED_FORM:
//                    paramValues[i] = requestInfo.getFormData();
//                    break;
            }

            paramTypes[i] = endpointMappingParam.getDataType();
        }

        // 执行method
        Object result = null;
        try {
            result = this.execute(mapping, paramTypes, paramValues);
        } catch (Throwable e) {
            // 全局异常处理
            result = BaseRes.fail(BaseResCode.FAIL.getCode(), e.getMessage());
        }
        buildResponse(result, response);
    }


    private boolean checkRequest(RestHttpRequest request, RestHttpResponse response) {
        List<String> supports = SupportHttpRequestType.supportTypes();
        boolean noneMatch = supports.stream().noneMatch(e -> e.equalsIgnoreCase(request.methodName()));
        if (noneMatch) {
            response.httpResponse().setStatus(HttpResponseStatus.METHOD_NOT_ALLOWED);
            response.httpResponse().content().writeBytes(Unpooled.copiedBuffer("", CharsetUtil.UTF_8));
            return false;
        }

        if (!SupportHttpContentType.APPLICATION_JSON.getContentType().equalsIgnoreCase(request.contentType())) {
            response.httpResponse().setStatus(HttpResponseStatus.METHOD_NOT_ALLOWED);
            response.httpResponse().content().writeBytes(Unpooled.copiedBuffer("content-type不支持", CharsetUtil.UTF_8));
            return false;
        }

        return true;
    }

    /**
     * 处理Options请求
     *
     * @param request  restful 请求
     * @param response restful 响应
     */
    private void handleOptionsRequest(RestHttpRequest request, RestHttpResponse response) {

        String header = request.headers().get("Access-Control-Request-Headers");
        if (header == null) return;

        List<String> requestHeaders = List.of(header.split(","));
        requestHeaders = requestHeaders.stream().filter(e -> !e.isEmpty()).toList();

        for (String requestHeader : requestHeaders) {
            if (!requestHeaderAllowed(requestHeader, response)) {
                response.httpResponse().setStatus(HttpResponseStatus.NOT_FOUND);
                response.httpResponse().content().writeBytes(Unpooled.copiedBuffer("", CharsetUtil.UTF_8));
                return;
            }
        }
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

    private void buildResponse(Object result, RestHttpResponse response) {

        String jsonStr = JacksonUtil.toJSONString(result);
        response.setContent(jsonStr);

        // 只支持 JSON 格式
        String contentType = "application/json; charset=UTF-8";
        response.setHeaders("Content-Type", contentType);


//        // 写入Cookie
//        Map<String, String> cookies = response.getCookies();
//        Set<Entry<String, String>> cookiesEntrySet = cookies.entrySet();
//        for (Entry<String, String> entry : cookiesEntrySet) {
//            Cookie cookie = new DefaultCookie(entry.getKey(), entry.getValue());
//            cookie.setPath("/");
//            httpResponse.headers().set(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
//        }

//        Map<String, String> responseHeaders = response.getHeaders();
////        Map<String, String> responseHeaders = HttpContextHolder.getResponse().getHeaders();
//        Set<Entry<String, String>> headersEntrySet = responseHeaders.entrySet();
//        for (Entry<String, String> entry : headersEntrySet) {
//            httpResponse.headers().add(entry.getKey(), entry.getValue());
//        }


//        return response.getChannelHandlerContext().writeAndFlush(httpResponse);
//        return HttpContextHolder.getResponse().getChannelHandlerContext().writeAndFlush(response);
    }

    /**
     * 得到Controller类的实例
     *
     * @return
     * @throws Exception
     */
    private Object execute(EndpointMapping mapping, Class<?>[] paramTypes, Object[] paramValues) throws Exception {
        Object instance = EndpointMappings.getSingleton(mapping.getClazz().getName());
        Method method = instance.getClass().getMethod(mapping.getMethod().getName(), paramTypes);
        return method.invoke(instance, paramValues);
    }

    /**
     * 输出响应结果
     *
     * @param responseEntity
     * @param jsonResponse
     * @return
     * @throws IOException
     */
    private ChannelFuture writeResponse(ResponseEntity<?> responseEntity, RestHttpRequest request, RestHttpResponse response, boolean jsonResponse) throws IOException {
        FullHttpResponse httpResponse = response.httpResponse();
//        httpResponse.setProtocolVersion(HTTP_1_1);

        HttpResponseStatus status = HttpResponseStatus.parseLine(String.valueOf(responseEntity.getStatus().value()));
//        httpResponse.setStatus(status);

        if (responseEntity.getBody() != null) {
            String jsonStr = JacksonUtil.toJSONString(responseEntity.getBody());
            httpResponse = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer(jsonStr, CharsetUtil.UTF_8));
        } else {
            httpResponse = new DefaultFullHttpResponse(HTTP_1_1, status);
        }

        String contentType = jsonResponse ? "application/json; charset=UTF-8" : "text/plain; charset=UTF-8";
        httpResponse.headers().set("Content-Type", contentType);

        // 写入Cookie
        Map<String, String> cookies = response.getCookies();
//        Map<String, String> cookies = HttpContextHolder.getResponse().getCookies();
        Set<Map.Entry<String, String>> cookiesEntrySet = cookies.entrySet();
        for (Map.Entry<String, String> entry : cookiesEntrySet) {
            Cookie cookie = new DefaultCookie(entry.getKey(), entry.getValue());
            cookie.setPath("/");
            httpResponse.headers().set(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
        }

        Map<String, String> responseHeaders = response.getHeaders();
//        Map<String, String> responseHeaders = HttpContextHolder.getResponse().getHeaders();
        Set<Map.Entry<String, String>> headersEntrySet = responseHeaders.entrySet();
        for (Map.Entry<String, String> entry : headersEntrySet) {
            httpResponse.headers().add(entry.getKey(), entry.getValue());
        }
        httpResponse.headers().setInt("Content-Length", httpResponse.content().readableBytes());
        return response.getChannelHandlerContext().writeAndFlush(httpResponse);
//        return HttpContextHolder.getResponse().getChannelHandlerContext().writeAndFlush(response);
    }

    /**
     * 得到路径变量
     *
     * @param url
     * @param mappingUrl
     * @param name
     * @return
     */
    private String getPathVariable(String url, String mappingUrl, String name) {
        String[] urlSplit = url.split("/");
        String[] mappingUrlSplit = mappingUrl.split("/");
        for (int i = 0; i < mappingUrlSplit.length; i++) {
            if (mappingUrlSplit[i].equals("{" + name + "}")) {
                if (urlSplit[i].contains("?")) {
                    return urlSplit[i].split("[?]")[0];
                }
                if (urlSplit[i].contains("&")) {
                    return urlSplit[i].split("&")[0];
                }
                return urlSplit[i];
            }
        }
        return null;
    }
}
