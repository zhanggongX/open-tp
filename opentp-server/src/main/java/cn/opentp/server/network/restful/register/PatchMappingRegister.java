package cn.opentp.server.network.restful.register;


import cn.opentp.server.network.restful.mapping.EndpointMappings;
import cn.opentp.server.network.restful.annotation.PatchMapping;
import cn.opentp.server.network.restful.mapping.EndpointMapping;

import java.lang.reflect.Method;

public class PatchMappingRegister extends AbstractMappingRegister {

    @Override
    String resolveMethodRequestUrl(Method method) {
        if(method.getAnnotation(PatchMapping.class) != null) {
            return method.getAnnotation(PatchMapping.class).value();
        }
        return "";
    }

//    @Override
//    String resolveHttpMethod() {
//        return "";
//    }

    @Override
    void registerMapping(String completeRequestUrl, EndpointMapping endpointMapping) {
        EndpointMappings.registerPatchMapping(completeRequestUrl, endpointMapping);
    }
}
