package cn.opentp.server.network.restful.register;

import cn.opentp.server.network.restful.SupportHttpRequestType;

import java.util.Map;

public class EndpointMappingRegisterHolder {

    public static final Map<SupportHttpRequestType, MappingRegister> MAPPING_REGISTER_MAP = Map.of(
            SupportHttpRequestType.GET, new GetMappingRegister(),
            SupportHttpRequestType.POST, new PostMappingRegister(),
            SupportHttpRequestType.PUT, new PutMappingRegister(),
            SupportHttpRequestType.PATCH, new PatchMappingRegister(),
            SupportHttpRequestType.DELETE, new DeleteMappingRegister());
}
