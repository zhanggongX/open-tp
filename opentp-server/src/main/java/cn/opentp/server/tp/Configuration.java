package cn.opentp.server.tp;

import cn.opentp.core.tp.ThreadPoolContext;
import cn.opentp.server.http.handler.HttpHandler;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {

    private volatile static Configuration INSTANCE;

    private Configuration() {
    }

    private final Map<String, ThreadPoolContext> tpCache = new ConcurrentHashMap<>();
    private final Map<String, Channel> tpChannel = new ConcurrentHashMap<>();
    private final Map<String, HttpHandler> endPoints = new ConcurrentHashMap<>();


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

    public Map<String, ThreadPoolContext> getTpCache() {
        return tpCache;
    }

    public Map<String, Channel> getTpChannel() {
        return tpChannel;
    }

    public Map<String, HttpHandler> getEndPoints() {
        return endPoints;
    }
}
