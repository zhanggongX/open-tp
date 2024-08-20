package cn.opentp.server.network.restful;

import cn.opentp.server.network.restful.http.RestHttpRequest;
import cn.opentp.server.network.restful.mapping.EndpointMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

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

    private static final Map<String, Object> singletons = new ConcurrentHashMap<>(128);

    /**
     * 注册Controller类的单例
     *
     * @param name
     * @param singleton
     */
    public static void registerSingleton(String name, Object singleton) {
        singletons.put(name, singleton);
    }

    /**
     * 得到单例
     *
     * @param name
     * @return
     */
    public static Object getSingleton(String name) {
        if (singletons.containsKey(name)) {
            return singletons.get(name);
        }

        Class<?> clazz = null;
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            log.error("Class not found: {}", name);
            return null;
        }
        Object instance = null;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Create class instance failure: {}", name);
            return null;
        }
        Object result = singletons.putIfAbsent(name, instance);
        if (result == null) {
            return instance;
        }
        return result;
    }


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

    /**
     * 得到控制器映射哈希表
     *
     * @param httpMethod
     * @return
     */
    private static Map<String, EndpointMapping> matchMappings(String httpMethod) {
        if (httpMethod == null) {
            return null;
        }
        return switch (httpMethod.toUpperCase()) {
            case "GET" -> getMappings;
            case "POST" -> postMappings;
            case "PUT" -> putMappings;
            case "DELETE" -> deleteMappings;
            case "PATCH" -> patchMappings;
            default -> null;
        };
    }

    public static EndpointMapping lookupMappings(RestHttpRequest request) {
        String uri = request.uri();
        String lookupPath = uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri;

        int paramStartIndex = lookupPath.indexOf("?");
        if (paramStartIndex > 0) {
            lookupPath = lookupPath.substring(0, paramStartIndex);
        }

        Map<String, EndpointMapping> endpointMappings = matchMappings(request.methodName());
        if (endpointMappings == null || endpointMappings.isEmpty()) {
            return null;
        }

        Set<Map.Entry<String, EndpointMapping>> entrySet = endpointMappings.entrySet();
        for (Map.Entry<String, EndpointMapping> entry : entrySet) {
            // 完全匹配
            if (entry.getKey().equals(lookupPath)) {
                return entry.getValue();
            }
        }

        for (Map.Entry<String, EndpointMapping> entry : entrySet) {
            // 包含PathVariable
            String matcher = builderMatcher(entry.getKey());
            if (lookupPath.startsWith(matcher)) {
                boolean matched = true;

                String[] lookupPathSplit = lookupPath.split("/");
                String[] mappingUrlSplit = entry.getKey().split("/");
                if (lookupPathSplit.length != mappingUrlSplit.length) {
                    continue;
                }
                for (int i = 0; i < lookupPathSplit.length; i++) {
                    if (!lookupPathSplit[i].equals(mappingUrlSplit[i])) {
                        if (!mappingUrlSplit[i].startsWith("{")) {
                            matched = false;
                            break;
                        }
                    }
                }
                if (matched) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    private static String builderMatcher(String url) {
        StringBuilder matcher = new StringBuilder();
        for (char c : url.toCharArray()) {
            if (c == '{') {
                break;
            }
            matcher.append(c);
        }
        return matcher.toString();
    }
}
