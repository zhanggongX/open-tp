package cn.opentp.client.configuration;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.thread.pool.ThreadPoolContext;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Configuration {

    private volatile static Configuration INSTANCE;

    // 服务器地址
    private final List<InetSocketAddress> serverAddresses = new CopyOnWriteArrayList<>();
    // 线程池增强对象缓存
    private final Map<String, ThreadPoolContext> ThreadPoolContextCache = new ConcurrentHashMap<>();
    // 重连配置
    private final NettyReconnectProperties nettyReconnectProperties = new NettyReconnectProperties();
    // 线程池信息上报配置
    private final ThreadPoolStateReportProperties threadPoolStateReportProperties = new ThreadPoolStateReportProperties();
    // 客户端认证信息
    private final ClientInfo clientInfo = new ClientInfo();
    // 线程池信息上报 socket
    private Channel threadPoolStateReportChannel;

    private Configuration() {
    }

    public Map<String, ThreadPoolContext> threadPoolContextCache() {
        return ThreadPoolContextCache;
    }

    public List<InetSocketAddress> serverAddresses() {
        return serverAddresses;
    }

    public NettyReconnectProperties nettyReconnectProperties() {
        return nettyReconnectProperties;
    }

    public ThreadPoolStateReportProperties threadPoolStateReportProperties() {
        return threadPoolStateReportProperties;
    }

    public Channel threadPoolStateReportChannel() {
        return threadPoolStateReportChannel;
    }

    public void threadPoolStateReportChannel(Channel threadPoolStateReportChannel) {
        this.threadPoolStateReportChannel = threadPoolStateReportChannel;
    }

    public ClientInfo clientInfo() {
        return clientInfo;
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
}
