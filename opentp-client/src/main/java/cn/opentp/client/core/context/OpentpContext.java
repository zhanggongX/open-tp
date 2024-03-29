package cn.opentp.client.core.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class OpentpContext {

    private static final Map<String, ThreadPoolExecutor> tps = new ConcurrentHashMap<>();

    public static void addTp(String key, ThreadPoolExecutor tp) {
        if (tps.containsKey(key)) {
            throw new IllegalArgumentException("thread pool key define dup");
        }
        tps.putIfAbsent(key, tp);
    }

    public static ThreadPoolExecutor getTp(String key) {
        return tps.get(key);
    }

    public static String tps() {
        StringBuilder stringBuilder = new StringBuilder();
        tps.values().forEach(e -> stringBuilder.append(e.toString()));
        return stringBuilder.toString();
    }
}
