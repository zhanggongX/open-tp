package cn.opentp.client.context;

import cn.opentp.core.tp.ThreadPoolWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OpentpContext {

    private static final Map<String, ThreadPoolWrapper> tpwCache = new ConcurrentHashMap<>();

    public static void cache(String key, ThreadPoolWrapper tpw) {
        if (tpwCache.containsKey(key)) {
            throw new IllegalArgumentException("thread pool key define dup");
        }
        tpwCache.putIfAbsent(key, tpw);
    }

    public static ThreadPoolWrapper get(String key) {
        return tpwCache.get(key);
    }

    public static String all() {
        StringBuilder stringBuilder = new StringBuilder();
        tpwCache.values().forEach(e -> stringBuilder.append(e.toString()));
        return stringBuilder.toString();
    }

    public static Map<String, ThreadPoolWrapper> allTps() {
        return tpwCache;
    }
}
