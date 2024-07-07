package cn.opentp.server;

import cn.opentp.core.auth.ServerInfo;
import cn.opentp.gossip.GossipBootstrap;
import cn.opentp.gossip.GossipProperties;
import cn.opentp.server.constant.OpentpServerConstant;
import cn.opentp.server.enums.DeployEnum;
import cn.opentp.server.gossip.GossipSendTask;
import cn.opentp.server.network.receive.ThreadPoolReceiveService;
import cn.opentp.server.network.restful.RestfulService;
import cn.opentp.server.util.PropertiesUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OpentpApp {

    /**
     * 全局唯一实例
     */
    private final static OpentpApp INSTANCE = new OpentpApp();
    /**
     * 服务端属性信息
     */
    private final Environment environment = new Environment();
    /**
     * 线程信息上报监听服务
     */
    private final ThreadPoolReceiveService receiveService = new ThreadPoolReceiveService();
    /**
     * restful 接口服务
     */
    private final RestfulService restfulService = new RestfulService();

    /**
     * 本机信息
     */
    private ServerInfo selfInfo;
    /**
     * 主类加载器
     */
    private ClassLoader primaryClassLoader;
    /**
     * 流言发布定时任务
     */
    private ScheduledExecutorService gossipPublishService;

    private OpentpApp() {
    }

    public static OpentpApp instance() {
        return INSTANCE;
    }

    public Environment environment() {
        return environment;
    }

    public ServerInfo selfInfo() {
        return selfInfo;
    }

    public ThreadPoolReceiveService receiveService() {
        return receiveService;
    }

    public RestfulService restfulService() {
        return restfulService;
    }

    public ScheduledExecutorService gossipPublishService() {
        return gossipPublishService;
    }

    public void run(Class<?> primaryClass) {
        this.primaryClassLoader = primaryClass.getClassLoader();
        this.selfInfo = new ServerInfo();

        prepareEnvironment();


        // 加载参数配置
//        if (!PropertiesUtil.loadCmdProps(opentpApp.properties(), args)) return;

//        ShutdownHook hook = new ShutdownHook();
        // 启动接收上报信息服务
        this.receiveService().start(selfInfo.getHost(), environment.getReceivePort());
//        hook.add(receiveReportServer);

        // 启动 restful 服务信息
        restfulService.start(selfInfo.getHost(), environment.getHttpPort());
//        hook.add(restServer);

        // 集群部署，启动 gossip
        if (environment.getDeploy() == DeployEnum.cluster) {
            this.gossipPublishService = Executors.newSingleThreadScheduledExecutor();

            GossipProperties properties = getGossipProperties(environment);
            // 初始化
            GossipBootstrap.init(properties);
            // 开启服务
            GossipBootstrap.start();
            this.gossipPublishService.scheduleAtFixedRate(new GossipSendTask(), 5, 5, TimeUnit.SECONDS);
        }

//        Runtime.getRuntime().addShutdownHook(hook);
    }

    private void prepareEnvironment() {
        // 加载配置
        PropertiesUtil.loadProps(this.primaryClassLoader, environment, OpentpServerConstant.DEFAULT_CONFIG_FILE);
        // 部署方式
        if (environment.getClusterNodes() != null && !environment.getClusterNodes().isEmpty()) {
            environment.setDeploy(DeployEnum.cluster);
        } else {
            environment.setDeploy(DeployEnum.standalone);
        }
    }

    /**
     * todo 逐步完善配置
     *
     * @param environment 运行环境
     * @return gossip 属性
     */
    private static GossipProperties getGossipProperties(Environment environment) {
        GossipProperties properties = new GossipProperties();
        properties.setCluster("opentp");
        properties.setHost("localhost");
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            properties.setHost(hostAddress);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        properties.setPort(environment.getTransportPort());
        properties.setNodeId(null);
        properties.setClusterNodes(environment.getClusterNodes());
        properties.setGossipInterval(5000);
        return properties;
    }
}
