package cn.opentp.server.network.restful;

import cn.opentp.core.util.JacksonUtil;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.exception.ResourceNotFoundException;
import cn.opentp.server.network.restful.convert.Converter;
import cn.opentp.server.network.restful.convert.ConverterFactory;
import cn.opentp.server.network.restful.http.RestHttpRequest;
import cn.opentp.server.network.restful.http.RestHttpResponse;
import cn.opentp.server.network.restful.http.RequestInfo;
import cn.opentp.server.network.restful.http.ResponseEntity;
import cn.opentp.server.network.restful.mapping.EndpointMapping;
import cn.opentp.server.network.restful.mapping.EndpointMappingParam;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 请求处理器类
 *
 * @author zg
 */
final class RequestHandler {

    /**
     * 处理请求
     */
    public ChannelFuture handleRequest(RestHttpRequest request, RestHttpResponse response) {

        // 查找匹配的Mapping
        EndpointMapping mapping = EndpointMappings.lookupMappings(request);
        if (mapping == null) {
//            HttpContextHolder.setRequest(requestInfo.getRequest());
//            HttpContextHolder.setResponse(requestInfo.getResponse());

            // 全局异常处理
            OpentpApp.instance().restfulService().getExceptionHandler().doHandle(new ResourceNotFoundException(), request, response);
            return null;
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
                    paramValues[i] = this.getPathVariable(request.uri(), mapping.getUrl(), endpointMappingParam.getName());
                    converter = ConverterFactory.create(endpointMappingParam.getDataType());
                    if (converter != null) {
                        paramValues[i] = converter.convert(paramValues[i]);
                    }
                    break;
//                case URL_ENCODED_FORM:
//                    paramValues[i] = requestInfo.getFormData();
//                    break;
//            case UPLOAD_FILE:
//                paramValues[i] = requestInfo.getFiles().size() > 0 ? requestInfo.getFiles().get(0) : null;
//                break;
//            case UPLOAD_FILES:
//                paramValues[i] = requestInfo.getFiles().size() > 0 ? requestInfo.getFiles() : null;
//                break;
            }
//            if (endpointMappingParam.getRequired() && paramValues[i] == null) {
//                throw new HandleRequestException("参数 " + cmp.getName() + " 为null");
//            }
            paramTypes[i] = endpointMappingParam.getDataType();
        }

        // 执行method
        try {
//            HttpContextHolder.setRequest(requestInfo.getRequest());
//            HttpContextHolder.setResponse(requestInfo.getResponse());
            Object result = this.execute(mapping, paramTypes, paramValues);

            if (!(result instanceof ResponseEntity)) {
                result = ResponseEntity.ok().build();
            }
            return writeResponse((ResponseEntity<?>) result, request, response, true);
        } catch (Exception e) {
            // 全局异常处理
            OpentpApp.instance().restfulService().getExceptionHandler().doHandle(e, request, response);
            return null;
        } finally {
//            HttpContextHolder.removeRequest();
//            HttpContextHolder.removeResponse();
        }
    }

    /**
     * 得到Controller类的实例
     *
     * @return
     * @throws Exception
     */
    private Object execute(EndpointMapping mapping, Class<?>[] paramTypes, Object[] paramValues) throws Exception {
//        Endpoint bean = EndpointMappingFactory.getBean(mapping.getClassName());
//        Object instance = null;
//        if (bean.getSingleton()) {
        Object instance = EndpointMappings.getSingleton(mapping.getClassName());
//        } else {
//            Class<?> clazz = Class.forName(mapping.getClassName());
//            instance = clazz.newInstance();
//        }
        Method method = instance.getClass().getMethod(mapping.getClassMethod(), paramTypes);
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
//        if (responseEntity.getBody() instanceof RandomAccessFile) {
//            return writeFileResponse(responseEntity, request, response);
//        }
        // 不支持文件

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
        Set<Entry<String, String>> cookiesEntrySet = cookies.entrySet();
        for (Entry<String, String> entry : cookiesEntrySet) {
            Cookie cookie = new DefaultCookie(entry.getKey(), entry.getValue());
            cookie.setPath("/");
            httpResponse.headers().set(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
        }

        Map<String, String> responseHeaders = response.getHeaders();
//        Map<String, String> responseHeaders = HttpContextHolder.getResponse().getHeaders();
        Set<Entry<String, String>> headersEntrySet = responseHeaders.entrySet();
        for (Entry<String, String> entry : headersEntrySet) {
            httpResponse.headers().add(entry.getKey(), entry.getValue());
        }
        httpResponse.headers().setInt("Content-Length", httpResponse.content().readableBytes());
        return response.getChannelHandlerContext().writeAndFlush(response);
//        return HttpContextHolder.getResponse().getChannelHandlerContext().writeAndFlush(response);
    }

    /**
     * 输出文件响应
     *
     * @param responseEntity
     * @return
     * @throws IOException
     */
//    private ChannelFuture writeFileResponse(ResponseEntity<?> responseEntity, RestHttpRequest request, RestHttpResponse response) throws IOException {
//        RandomAccessFile raf = (RandomAccessFile) responseEntity.getBody();
//        long fileLength = raf.length();
//
////        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
//        FullHttpResponse httpResponse = response.httpResponse();
//
//        HttpUtil.setContentLength(response.httpResponse(), fileLength);
//        if (responseEntity.getMimetype() != null && !responseEntity.getMimetype().trim().equals("")) {
//            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, responseEntity.getMimetype());
//        }
//        if (responseEntity.getFileName() != null && !responseEntity.getFileName().trim().equals("")) {
//            String fileName = new String(responseEntity.getFileName().getBytes("gb2312"), "ISO8859-1");
//            httpResponse.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
//        }
//        if (HttpUtil.isKeepAlive(request.httpRequest())) {
//            httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
//        }
//
//        ChannelHandlerContext ctx = response.getChannelHandlerContext();
//        ctx.write(httpResponse);
//        ChannelFuture sendFileFuture;
//        ChannelFuture lastContentFuture = null;
//        if (ctx.pipeline().get(SslHandler.class) == null) {
//            sendFileFuture =
//                    ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
//            // Write the end marker.
//            lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
//        } else {
//            sendFileFuture = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)),
//                    ctx.newProgressivePromise());
//            // HttpChunkedInput will write the end marker (LastHttpContent) for us.
//            lastContentFuture = sendFileFuture;
//        }
//        return lastContentFuture;
//    }

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
