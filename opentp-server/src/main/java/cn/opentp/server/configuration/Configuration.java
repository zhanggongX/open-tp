package cn.opentp.server.configuration;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.auth.ServerInfo;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.server.rest.EndpointMapping;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {

    private volatile static Configuration INSTANCE;

    // 服务端属性信息
    private final OpentpProperties props = new OpentpProperties();
    // app 类加载器
    private final ClassLoader appClassLoader = Configuration.class.getClassLoader();
    // rest endpoint 映射
    private final EndpointMapping endpointMapping = new EndpointMapping();
    // key = appId, value = 所有连接上来的客户端
    private final Map<String, List<ClientInfo>> appKeyClientCache = new ConcurrentHashMap<>();
    // key = 客户端, value = channel
    private final Map<ClientInfo, Channel> clientChannelCache = new ConcurrentHashMap<>();
    private final Map<String, Channel> clientKeyChannelCache = new ConcurrentHashMap<>();
    // key = 客户端, value = <key = threadKey, value = threadPoolSate>
    private final Map<ClientInfo, Map<String, ThreadPoolState>> clientThreadPoolStatesCache = new ConcurrentHashMap<>();
    private final Map<String, Map<String, ThreadPoolState>> clientKeyThreadPoolStatesCache = new ConcurrentHashMap<>();
    // key = licenseKey value = ClientInfo
    private final Map<String, ClientInfo> licenseKeyClientCache = new ConcurrentHashMap<>();
    // 集群 key = 线程客户端信息， value = 所在的服务端信息
    private final Map<ClientInfo, ServerInfo> clusterClientInfoCache = new ConcurrentHashMap<>();
    private final Map<ServerInfo, List<ClientInfo>> clusterServerInfoCache = new ConcurrentHashMap<>();

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

    public OpentpProperties properties() {
        return props;
    }

    public ClassLoader appClassLoader() {
        return appClassLoader;
    }

    public EndpointMapping endpointMapping() {
        return endpointMapping;
    }

    public Map<String, List<ClientInfo>> appKeyClientCache() {
        return appKeyClientCache;
    }

    public Map<ClientInfo, Channel> clientChannelCache() {
        return clientChannelCache;
    }

    public Map<ClientInfo, Map<String, ThreadPoolState>> clientThreadPoolStatesCache() {
        return clientThreadPoolStatesCache;
    }

    public Map<String, ClientInfo> licenseKeyClientCache() {
        return licenseKeyClientCache;
    }

    public Map<String, Map<String, ThreadPoolState>> clientKeyThreadPoolStatesCache() {
        return clientKeyThreadPoolStatesCache;
    }

    public Map<String, Channel> clientKeyChannelCache() {
        return clientKeyChannelCache;
    }

    public Map<ClientInfo, ServerInfo> clusterClientInfoCache() {
        return clusterClientInfoCache;
    }

    public Map<ServerInfo, List<ClientInfo>> clusterServerInfoCache() {
        return clusterServerInfoCache;
    }
}
