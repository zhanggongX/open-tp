package cn.opentp.server.network.restful.http;

import io.netty.handler.codec.http.FullHttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求信息类
 *
 * @author zg
 */
public final class RequestInfo {

    private FullHttpRequest request;

    private RestHttpResponse response;

    private Map<String, Object> parameters = new HashMap<>();

    private Map<String, String> headers = new HashMap<>();

    private String body;

    private Map<String, String> formData = new HashMap<>();

//    private List<MultipartFile> files = new ArrayList<>(8);

    public FullHttpRequest getRequest() {
        return this.request;
    }

    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }

    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getFormData() {
        return this.formData;
    }

    public RestHttpResponse getResponse() {
        return this.response;
    }

    public void setResponse(RestHttpResponse response) {
        this.response = response;
    }

//    public List<MultipartFile> getFiles() {
//        return files;
//    }
}
