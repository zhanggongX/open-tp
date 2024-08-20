package cn.opentp.server.network.restful.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RestHttpRequest {

    private final FullHttpRequest httpRequest;

    private final Map<String, Object> params = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();

    private String requestBody;


    public RestHttpRequest(FullHttpRequest httpRequest) {
        this.httpRequest = httpRequest;

        QueryStringDecoder urlDecoder = new QueryStringDecoder(httpRequest.uri());
        Set<Map.Entry<String, List<String>>> entrySet = urlDecoder.parameters().entrySet();
        entrySet.forEach(entry -> {
            params.put(entry.getKey(), entry.getValue().get(0));
        });

        List<Map.Entry<String, String>> entries = httpRequest.headers().entries();
        for (Map.Entry<String, String> entry : entries) {
            headers.put(entry.getKey(), entry.getValue());
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

    public void setRequestBody() {
        httpRequest.content().toString(StandardCharsets.UTF_8);
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

//    public void putParam(String paramName, String param) {
//        params.put(paramName, param);
//    }

//    public Set<String> headerNames() {
////        HttpHeaders headers = httpRequest.headers();
//        return httpRequest.headers().names();
//    }
}
