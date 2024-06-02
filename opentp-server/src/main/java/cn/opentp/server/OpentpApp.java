package cn.opentp.server;

import cn.opentp.core.auth.ServerInfo;
import cn.opentp.server.network.receive.ThreadPoolReceiveService;
import cn.opentp.server.network.restful.RestfulService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class OpentpApp {

    private final static OpentpApp INSTANCE = new OpentpApp();

    // 服务端属性信息
    private final OpentpProperties properties = new OpentpProperties();
    // app 类加载器
    private final ClassLoader appClassLoader = OpentpApp.class.getClassLoader();
    // 本机信息
    private final ServerInfo serverInfo = new ServerInfo();

    // 线程信息上报监听服务
    private final ThreadPoolReceiveService reportService = new ThreadPoolReceiveService();
    // restful 接口服务
    private final RestfulService restfulService = new RestfulService();

    // 流言发布定时任务
    private final ScheduledExecutorService gossipPublishExecutor = Executors.newSingleThreadScheduledExecutor();

    private OpentpApp() {
    }

    public static OpentpApp instance() {
        return INSTANCE;
    }

    public OpentpProperties properties() {
        return properties;
    }

    public ClassLoader appClassLoader() {
        return appClassLoader;
    }

    public ServerInfo selfInfo() {
        return serverInfo;
    }

    public ThreadPoolReceiveService receiveService() {
        return reportService;
    }

    public RestfulService restfulService() {
        return restfulService;
    }

    public ScheduledExecutorService gossipPublishExecutor() {
        return gossipPublishExecutor;
    }
}
