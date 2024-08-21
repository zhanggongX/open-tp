package cn.opentp.server.network.restful.http;

import cn.opentp.server.network.restful.register.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 本项目仅支持的 Http RESTFul 请求类型
 *
 * @author zg
 */
public enum SupportHttpRequestType {
    GET(new GetMappingRegister()),
    POST(new PostMappingRegister()),
    PUT(new PutMappingRegister()),
    PATCH(new PatchMappingRegister()),
    DELETE(new DeleteMappingRegister());

    private final MappingRegister mappingRegister;

    SupportHttpRequestType(MappingRegister mappingRegister) {
        this.mappingRegister = mappingRegister;
    }

    public static List<String> supportTypes() {
        List<SupportHttpRequestType> allTypes = List.of(SupportHttpRequestType.values());
        return allTypes.stream().map(SupportHttpRequestType::name).collect(Collectors.toList());
    }

    public static String supports() {
        return String.join(",", supportTypes());
    }

    public MappingRegister getMappingRegister() {
        return mappingRegister;
    }
}
