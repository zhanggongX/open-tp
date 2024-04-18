package cn.opentp.client.configuration;

import cn.opentp.core.tp.ThreadPoolContext;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Configuration {

    private volatile static Configuration INSTANCE;

    public static final int DEFAULT_PORT = 9527;
    public static final String SERVER_SPLITTER = ",";
    public static final String SERVER_PORT_SPLITTER = ":";


    /**
     * 服务器地址
     */
    private final List<InetSocketAddress> serverAddresses = new CopyOnWriteArrayList<>();

    /**
     * 线程池增强对象缓存。
     */
    private final Map<String, ThreadPoolContext> ThreadPoolContextCache = new ConcurrentHashMap<>();


    private Configuration() {
    }

    public static Configuration configuration() {
        if (INSTANCE == null) {
            synchronized (Configuration.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Configuration();
                }
            }
        }
        return INSTANCE;
    }

    public Map<String, ThreadPoolContext> threadPoolContextCache() {
        return ThreadPoolContextCache;
    }

    public List<InetSocketAddress> serverAddresses() {
        return serverAddresses;
    }
}