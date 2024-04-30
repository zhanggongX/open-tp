package cn.opentp.server;

import cn.opentp.server.configuration.Configuration;
import cn.opentp.server.configuration.OpentpProperties;
import cn.opentp.server.constant.Constant;
import cn.opentp.server.report.ReportServer;
import cn.opentp.server.rest.RestServer;
import cn.opentp.server.rocksdb.OpentpRocksDB;
import cn.opentp.server.transport.TransportServer;
import cn.opentp.server.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * opentp 启动类
 */
public class OpentpBootstrap {

    private static final Logger log = LoggerFactory.getLogger(OpentpBootstrap.class);

    private OpentpBootstrap() {
    }

    public static void main(String[] args) throws InterruptedException {

        // 全局唯一配置实例
        Configuration configuration = Configuration.configuration();

        // 加载配置
        PropertiesUtil.loadProps(configuration.appClassLoader(), configuration.properties(), Constant.DEFAULT_CONFIG_FILE);

        // 加载参数配置
        if (!PropertiesUtil.loadCmdProps(configuration.properties(), args)) return;

        startServers();
    }

    private static void startServers() {
        Configuration configuration = Configuration.configuration();

        OpentpProperties properties = configuration.properties();
        ReportServer reportServer = new ReportServer();
        reportServer.start(properties.getReportServerPort());

        RestServer restServer = new RestServer();
        restServer.start(properties.getHttpServerPort());

        TransportServer transportServer = new TransportServer();
        transportServer.start(properties.getTransportServerPort());

        ShutdownHook shutdownHook = new ShutdownHook();
        shutdownHook.add(restServer);
        shutdownHook.add(reportServer);
        shutdownHook.add(transportServer);
        shutdownHook.add(OpentpRocksDB.rocksDB());

        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }
}
