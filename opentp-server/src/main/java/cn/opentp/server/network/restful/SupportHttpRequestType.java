package cn.opentp.server.network.restful;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 本项目仅支持的 Http RESTFul 请求类型
 *
 * @author zg
 */
public enum SupportHttpRequestType {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE;

    public static String supportedHttpMethods() {
        List<SupportHttpRequestType> allTypes = List.of(SupportHttpRequestType.values());
        return allTypes.stream().map(SupportHttpRequestType::name).collect(Collectors.joining(","));
    }
}
