package cn.opentp.server;

import cn.opentp.gossip.Gossip;
import cn.opentp.gossip.GossipProperties;
import cn.opentp.server.constant.OpentpServerConstant;
import cn.opentp.server.enums.DeployEnum;
import cn.opentp.server.report.ReceiveReportServer;
import cn.opentp.server.rest.RestServer;
import cn.opentp.server.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * opentp 启动类
 */
public class Opentp {

    private static final Logger log = LoggerFactory.getLogger(Opentp.class);

    // 全局唯一配置实例
    private static final OpentpApp opentpApp = OpentpApp.instance();

    private Opentp() {
    }

    public static void main(String[] args) throws InterruptedException {
        // 加载配置
        PropertiesUtil.loadProps(opentpApp.appClassLoader(), opentpApp.properties(), OpentpServerConstant.DEFAULT_CONFIG_FILE);

        // 加载参数配置
        if (!PropertiesUtil.loadCmdProps(opentpApp.properties(), args)) return;

        startServers();
    }

    private static void startServers() {

        OpentpProperties config = opentpApp.properties();

        ShutdownHook hook = new ShutdownHook();
        // 启动接收上报信息服务
        ReceiveReportServer receiveReportServer = new ReceiveReportServer();
        receiveReportServer.start(config.getReportPort());
        hook.add(receiveReportServer);

        // 启动 restful 服务信息
        RestServer restServer = new RestServer();
        restServer.start(config.getHttpPort());
        hook.add(restServer);

        DeployEnum deploy = config.getDeploy();
        // 集群部署，启动 gossip
        if (deploy == DeployEnum.cluster) {
            GossipProperties properties = getGossipProperties(config);
            // 初始化
            Gossip.init(properties);
            // 开启服务
            Gossip.start();
        }

        Runtime.getRuntime().addShutdownHook(hook);
    }

    /**
     * todo 逐步完善配置
     *
     * @param config yml
     * @return gossip 属性
     */
    private static GossipProperties getGossipProperties(OpentpProperties config) {
        GossipProperties properties = new GossipProperties();
        properties.setCluster("opentp");
        properties.setHost("localhost");
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            properties.setHost(hostAddress);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        properties.setPort(config.getGossipPort());
        properties.setNodeId(null);
        properties.setClusterNodes(config.getCluster());
        properties.setGossipInterval(5000);
        return properties;
    }
}
