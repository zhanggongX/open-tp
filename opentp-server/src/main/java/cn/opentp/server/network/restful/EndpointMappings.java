package cn.opentp.server.network.restful;

import cn.opentp.server.exception.EndpointUnSupportException;
import cn.opentp.server.network.restful.endpoint.*;
import cn.opentp.server.network.restful.mapping.EndpointMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * endpoint 映射
 */
public class EndpointMappings {

    private static final Logger log = LoggerFactory.getLogger(EndpointMappings.class);

    private static final Map<String, EndpointMapping> patchMappings = new ConcurrentHashMap<>();
    private static final Map<String, EndpointMapping> getMappings = new ConcurrentHashMap<>();
    private static final Map<String, EndpointMapping> postMappings = new ConcurrentHashMap<>();
    private static final Map<String, EndpointMapping> putMappings = new ConcurrentHashMap<>();
    private static final Map<String, EndpointMapping> deleteMappings = new ConcurrentHashMap<>();


    public static synchronized void registerDeleteMapping(String completeRequestUrl, EndpointMapping endpointMapping) {
        EndpointMapping added = deleteMappings.get(completeRequestUrl);
        if (added != null) {
            log.error("重复的 url: {}", completeRequestUrl);
            throw new IllegalArgumentException("URL重复");
        }
        deleteMappings.put(completeRequestUrl, endpointMapping);
    }

    public static synchronized void registerGetMapping(String completeRequestUrl, EndpointMapping endpointMapping) {
        EndpointMapping added = getMappings.get(completeRequestUrl);
        if (added != null) {
            log.error("重复的 url: {}", completeRequestUrl);
            throw new IllegalArgumentException("URL重复");
        }
        getMappings.put(completeRequestUrl, endpointMapping);
    }

    public static synchronized void registerPatchMapping(String completeRequestUrl, EndpointMapping endpointMapping) {
        EndpointMapping added = patchMappings.get(completeRequestUrl);
        if (added != null) {
            log.error("重复的 url: {}", completeRequestUrl);
            throw new IllegalArgumentException("URL重复");
        }
        patchMappings.put(completeRequestUrl, endpointMapping);
    }

    public static synchronized void registerPostMapping(String completeRequestUrl, EndpointMapping endpointMapping) {
        EndpointMapping added = postMappings.get(completeRequestUrl);
        if (added != null) {
            log.error("重复的 url: {}", completeRequestUrl);
            throw new IllegalArgumentException("URL重复");
        }
        postMappings.put(completeRequestUrl, endpointMapping);
    }

    public static synchronized void registerPutMapping(String completeRequestUrl, EndpointMapping endpointMapping) {
        EndpointMapping added = putMappings.get(completeRequestUrl);
        if (added != null) {
            log.error("重复的 url: {}", completeRequestUrl);
            throw new IllegalArgumentException("URL重复");
        }
        putMappings.put(completeRequestUrl, endpointMapping);
    }
}
