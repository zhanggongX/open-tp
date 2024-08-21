package cn.opentp.server.network.restful.register;

import cn.opentp.server.network.restful.mapping.EndpointMappings;
import cn.opentp.server.network.restful.annotation.DeleteMapping;
import cn.opentp.server.network.restful.mapping.EndpointMapping;

import java.lang.reflect.Method;

public class DeleteMappingRegister extends AbstractMappingRegister {

    @Override
    String resolveMethodRequestUrl(Method method) {
        if (method.getAnnotation(DeleteMapping.class) != null) {
            return method.getAnnotation(DeleteMapping.class).value();
        }
        return "";
    }

//    @Override
//    String resolveHttpMethod() {
//        return SupportHttpRequestType.DELETE.name();
//    }

    @Override
    void registerMapping(String completeRequestUrl, EndpointMapping endpointMapping) {
        EndpointMappings.registerDeleteMapping(completeRequestUrl, endpointMapping);
    }
}
