package cn.opentp.server.network.restful.register;

import cn.opentp.server.network.restful.mapping.EndpointMappings;
import cn.opentp.server.network.restful.annotation.PostMapping;
import cn.opentp.server.network.restful.mapping.EndpointMapping;

import java.lang.reflect.Method;

/**
 * POST
 */
public class PostMappingRegister extends AbstractMappingRegister {

    @Override
    String resolveMethodRequestUrl(Method method) {
        if (method.getAnnotation(PostMapping.class) != null) {
            return method.getAnnotation(PostMapping.class).value();
        }
        return "";
    }

    @Override
    void registerMapping(String completeRequestUrl, EndpointMapping endpointMapping) {
        EndpointMappings.registerPostMapping(completeRequestUrl, endpointMapping);
    }
}
