package cn.opentp.server.network.restful.register;

import cn.opentp.server.network.restful.EndpointMappings;
import cn.opentp.server.network.restful.annotation.GetMapping;
import cn.opentp.server.network.restful.mapping.EndpointMapping;

import java.lang.reflect.Method;

/**
 * GET
 */
public class GetMappingRegister extends AbstractMappingRegister {

    @Override
    String resolveMethodRequestUrl(Method method) {
        if (method.getAnnotation(GetMapping.class) != null) {
            return method.getAnnotation(GetMapping.class).value();
        }
        return "";
    }

//    @Override
//    String resolveHttpMethod() {
//        return "";
//    }

    @Override
    void registerMapping(String completeRequestUrl, EndpointMapping endpointMapping) {
        EndpointMappings.registerGetMapping(completeRequestUrl, endpointMapping);
    }
}
