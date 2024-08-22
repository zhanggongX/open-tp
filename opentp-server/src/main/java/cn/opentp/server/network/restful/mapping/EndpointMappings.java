package cn.opentp.server.network.restful.mapping;

import cn.opentp.server.exception.ResourceNotFoundException;
import cn.opentp.server.network.restful.http.RestHttpRequest;
import com.sun.jdi.request.DuplicateRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
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

    private static final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();
    private static final Object lock = new Object();

    /**
     * 得到单例
     *
     * @param clazz 类
     * @return 类对象
     */
    public static Object getSingleton(Class<?> clazz) {
        return singletons.get(clazz);
    }

    private static void registerSingleton(Class<?> clazz) {
        if (!singletons.containsKey(clazz)) {
            synchronized (lock) {
                if (!singletons.containsKey(clazz)) {
                    Object instance = null;
                    try {
                        instance = clazz.getDeclaredConstructor().newInstance();
                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                             IllegalAccessException e) {
                        log.error("Create class instance failure: {}, : ", clazz.getName(), e);
                        throw new ResourceNotFoundException("class[" + clazz.getName() + "]创建对象失败");
                    }
                    singletons.put(clazz, instance);
                }
            }
        }
    }


    public static synchronized void registerDeleteMapping(String completeRequestUrl, EndpointMapping endpointMapping) {
        EndpointMapping added = deleteMappings.get(completeRequestUrl);
        if (added != null) {
            throw new RuntimeException("URL重复：" + completeRequestUrl);
        }
        deleteMappings.put(completeRequestUrl, endpointMapping);
        registerSingleton(endpointMapping.getClazz());
    }

    public static synchronized void registerGetMapping(String completeRequestUrl, EndpointMapping endpointMapping) {
        EndpointMapping added = getMappings.get(completeRequestUrl);
        if (added != null) {
            throw new RuntimeException("URL重复：" + completeRequestUrl);
        }
        getMappings.put(completeRequestUrl, endpointMapping);
        registerSingleton(endpointMapping.getClazz());
    }

    public static synchronized void registerPatchMapping(String completeRequestUrl, EndpointMapping endpointMapping) {
        EndpointMapping added = patchMappings.get(completeRequestUrl);
        if (added != null) {
            throw new RuntimeException("URL重复：" + completeRequestUrl);
        }
        patchMappings.put(completeRequestUrl, endpointMapping);
        registerSingleton(endpointMapping.getClazz());
    }

    public static synchronized void registerPostMapping(String completeRequestUrl, EndpointMapping endpointMapping) {
        EndpointMapping added = postMappings.get(completeRequestUrl);
        if (added != null) {
            throw new RuntimeException("URL重复：" + completeRequestUrl);
        }
        postMappings.put(completeRequestUrl, endpointMapping);
        registerSingleton(endpointMapping.getClazz());
    }

    public static synchronized void registerPutMapping(String completeRequestUrl, EndpointMapping endpointMapping) {
        EndpointMapping added = putMappings.get(completeRequestUrl);
        if (added != null) {
            throw new RuntimeException("URL重复：" + completeRequestUrl);
        }
        putMappings.put(completeRequestUrl, endpointMapping);
        registerSingleton(endpointMapping.getClazz());
    }

    /**
     * 得到控制器映射哈希表
     *
     * @param httpMethod http 请求类型
     * @return endpoint 映射哈希表
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

    public static EndpointMapping matchEndpointMapping(RestHttpRequest request) {
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
