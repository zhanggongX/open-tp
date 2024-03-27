package cn.opentp.client.core.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class OpentpContext {

    private final Map<String, ThreadPoolExecutor> tps = new ConcurrentHashMap<>();

    public void addTp(String key, ThreadPoolExecutor tp) {
        if (tps.containsKey(key)) {
            throw new IllegalArgumentException("thread pool key define dup");
        }
        tps.putIfAbsent(key, tp);
    }

    public ThreadPoolExecutor getTp(String key) {
        return tps.get(key);
    }
}
