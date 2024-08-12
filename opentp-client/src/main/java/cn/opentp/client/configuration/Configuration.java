package cn.opentp.client.configuration;

import cn.opentp.client.network.ThreadPoolReportService;
import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.thread.pool.ThreadPoolWrapper;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Configuration {

    private static final Configuration INSTANCE = new Configuration();

    // 服务器地址
    private final List<InetSocketAddress> serverAddresses = new CopyOnWriteArrayList<>();
    // 线程池增强对象缓存
    private final Map<String, ThreadPoolWrapper> ThreadPoolContextCache = new ConcurrentHashMap<>();
    // 重连配置
    private final ReconnectProperties reconnectProps = new ReconnectProperties();
    // 线程池信息上报配置
    private final ReportProperties reportProps = new ReportProperties();
    // 客户端认证信息
    private final ClientInfo clientInfo = new ClientInfo();
    // 线程信息上报服务
    private final ThreadPoolReportService reportService = new ThreadPoolReportService();

    private Configuration() {
    }

    public static Configuration _cfg() {
        return INSTANCE;
    }

    public Map<String, ThreadPoolWrapper> threadPoolContextCache() {
        return ThreadPoolContextCache;
    }

    public List<InetSocketAddress> serverAddresses() {
        return serverAddresses;
    }

    public ReconnectProperties reconnectProps() {
        return reconnectProps;
    }

    public ReportProperties reportProps() {
        return reportProps;
    }

    public ClientInfo clientInfo() {
        return clientInfo;
    }

    public ThreadPoolReportService reportService() {
        return reportService;
    }

}
