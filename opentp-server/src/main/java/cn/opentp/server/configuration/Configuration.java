package cn.opentp.server.configuration;

import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.server.http.handler.HttpHandler;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {

    private volatile static Configuration INSTANCE;

    private final Map<String, ThreadPoolState> theadPoolStateCache = new ConcurrentHashMap<>();
    private final Map<String, Channel> channelCache = new ConcurrentHashMap<>();
    private final Map<String, HttpHandler> endPoints = new ConcurrentHashMap<>();


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

    public Map<String, ThreadPoolState> theadPoolStateCache() {
        return theadPoolStateCache;
    }

    public Map<String, Channel> channelCache() {
        return channelCache;
    }

    public Map<String, HttpHandler> endPoints() {
        return endPoints;
    }
}
