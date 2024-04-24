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

    // todo 后续迭代认证服务
    private static final String DEFAULT_USER = "admin";
    private static final String DEFAULT_PW = "123456";
    // todo 后续迭代去掉
    private static final String ADMIN_DEFAULT_APP = "opentp";
    private static final String ADMIN_DEFAULT_SECRET = "opentp-secret";
    // 消息原型
    public static final OpentpMessage OPENTP_MSG_PROTO = new OpentpMessage(OpentpMessageConstant.MAGIC, OpentpMessageConstant.VERSION);

    // 全局配置开始
    private final Map<String, ThreadPoolState> threadPoolStateCache = new ConcurrentHashMap<>();
    private final Map<String, Channel> channelCache = new ConcurrentHashMap<>();
    private final Map<String, HttpHandler> endPoints = new ConcurrentHashMap<>();
    // 全局配置结束

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
