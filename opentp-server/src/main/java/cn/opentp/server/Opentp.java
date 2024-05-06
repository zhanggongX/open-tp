package cn.opentp.server;

import cn.opentp.server.config.Config;
import cn.opentp.server.constant.OpentpServerConstant;
import cn.opentp.server.enums.DeployEnum;
import cn.opentp.server.report.ReceiveReportServer;
import cn.opentp.server.rest.RestServer;
import cn.opentp.server.transport.TransportServer;
import cn.opentp.server.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        PropertiesUtil.loadProps(opentpApp.appClassLoader(), opentpApp.cfg(), OpentpServerConstant.DEFAULT_CONFIG_FILE);

        // 加载参数配置
        if (!PropertiesUtil.loadCmdProps(opentpApp.cfg(), args)) return;

        startServers();
    }

    private static void startServers() {

        Config config = opentpApp.cfg();

        ShutdownHook hook = new ShutdownHook();
        // 启动接收上报信息服务
        ReceiveReportServer receiveReportServer = new ReceiveReportServer();
        receiveReportServer.start(config.getReportServerPort());
        hook.add(receiveReportServer);

        // 启动 restful 服务信息
        RestServer restServer = new RestServer();
        restServer.start(config.getHttpServerPort());
        hook.add(restServer);

        DeployEnum deploy = config.getDeploy();
        String master = config.getMaster();
        // 集群部署，且 master 配置信息为空，则认为是主节点，启动 TransportServer
        if (deploy == DeployEnum.cluster && (master == null || !master.isEmpty())) {
            TransportServer transportServer = new TransportServer();
            transportServer.start(config.getTransportServerPort());
            hook.add(transportServer);
        }

        Runtime.getRuntime().addShutdownHook(hook);
    }
}
