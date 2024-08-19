package cn.opentp.server.network.restful;

import cn.opentp.core.util.JacksonUtil;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.constant.OpentpServerConstant;
import cn.opentp.server.network.restful.endpoint.Endpoint;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class EndpointDispatcher {

    private static final Logger log = LoggerFactory.getLogger(EndpointDispatcher.class);

    public static void dispatcher(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        try {
            EndpointDispatcher.doDispatcher(httpRequest, httpResponse);
        } catch (Exception e) {
            log.error("endpoint handler exception, ", e);
            retFail(httpResponse, e);
        }
    }

    private static void doDispatcher(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        String uri = httpRequest.uri();
        HttpMethod method = httpRequest.method();

        String endpoint = getEndpoint(uri);

        EndpointMappings endpointMapping = OpentpApp.instance().restfulService().endpointMapping();
        Endpoint httpHandler = endpointMapping.mappingHandler(endpoint);

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
}
