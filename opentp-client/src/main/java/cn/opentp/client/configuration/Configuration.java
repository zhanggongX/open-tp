package cn.opentp.client.configuration;

import cn.opentp.core.tp.ThreadPoolWrapper;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Configuration {

    private volatile static Configuration INSTANCE;

    /**
     * 服务器地址
     */
    private final List<InetSocketAddress> serverAddresses = new CopyOnWriteArrayList<>();

    /**
     * 线程池增强对象缓存。
     */
    private final Map<String, ThreadPoolWrapper> threadPoolWrapperCache = new ConcurrentHashMap<>();


    private Configuration() {
    }

    public static Configuration opentpClientConfig() {
        if (INSTANCE == null) {
            synchronized (Configuration.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Configuration();
                }
            }
        }
        return INSTANCE;
    }

    public Map<String, ThreadPoolWrapper> getThreadPoolWrapperCache() {
        return threadPoolWrapperCache;
    }

    public List<InetSocketAddress> getServerAddresses() {
        return serverAddresses;
    }
}
