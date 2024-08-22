package cn.opentp.server.network.restful.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * RESTFul 请求对象
 */
public class RestHttpRequest {

    // 请求对象
    private final FullHttpRequest httpRequest;

    // 请求参数
    private final Map<String, Object> params = new HashMap<>();
    // 请求头
    private final Map<String, String> headers = new HashMap<>();

    // 请求内容类型
    private String contentType;
    // 请求体
    private String requestBody;


    public RestHttpRequest(FullHttpRequest httpRequest) {
        this.httpRequest = httpRequest;

        QueryStringDecoder urlDecoder = new QueryStringDecoder(httpRequest.uri());
        urlDecoder.parameters().forEach((key, value) -> params.put(key, value.get(0)));

        httpRequest.headers().forEach(e -> headers.put(e.getKey(), e.getValue()));

        this.contentType = httpRequest.headers().get("Content-Type");
        if (this.contentType != null) {
            this.contentType = this.contentType.split(";")[0];
        }else {
            this.contentType = SupportHttpContentType.APPLICATION_JSON.getContentType();
        }

        // 非 GET 请求，记录请求体。
        if (!httpRequest.method().name().equalsIgnoreCase("GET")) {
            this.requestBody = httpRequest.content().toString(StandardCharsets.UTF_8);
        }
    }

    public String methodName() {
        return httpRequest.method().name();
    }

    public HttpHeaders headers() {
        return httpRequest.headers();
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String uri() {
        return httpRequest.uri();
    }

    public FullHttpRequest httpRequest() {
        return httpRequest;
    }

    public Object getParam(String paramName) {
        return params.get(paramName);
    }

    public String contentType() {
        return contentType;

    }
}
