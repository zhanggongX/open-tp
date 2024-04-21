package cn.opentp.server.configuration;

import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageConstant;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.server.http.handler.HttpHandler;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {

    private volatile static Configuration INSTANCE;

    private final Map<String, ThreadPoolState> threadPoolStateCache = new ConcurrentHashMap<>();
    private final Map<String, Channel> channelCache = new ConcurrentHashMap<>();
    private final Map<String, HttpHandler> endPoints = new ConcurrentHashMap<>();
    // 消息原型
    public static final OpentpMessage OPENTP_MSG_PROTO = new OpentpMessage(OpentpMessageConstant.MAGIC, OpentpMessageConstant.VERSION);


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

    public Map<String, ThreadPoolState> threadPoolStateCache() {
        return threadPoolStateCache;
    }

    public Map<String, Channel> channelCache() {
        return channelCache;
    }

    public Map<String, HttpHandler> endPoints() {
        return endPoints;
    }
}
