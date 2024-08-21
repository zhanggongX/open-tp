package cn.opentp.server.network.restful.register;

import cn.opentp.server.network.restful.mapping.EndpointMappings;
import cn.opentp.server.network.restful.annotation.PutMapping;
import cn.opentp.server.network.restful.mapping.EndpointMapping;

import java.lang.reflect.Method;

/**
 * PUT
 */
public class PutMappingRegister extends AbstractMappingRegister {
    @Override
    String resolveMethodRequestUrl(Method method) {
        if (method.getAnnotation(PutMapping.class) != null) {
            return method.getAnnotation(PutMapping.class).value();
        }
        return "";
    }

//    @Override
//    String resolveHttpMethod() {
//        return "";
//    }

    @Override
    void registerMapping(String completeRequestUrl, EndpointMapping endpointMapping) {
        EndpointMappings.registerPutMapping(completeRequestUrl, endpointMapping);
    }
}
