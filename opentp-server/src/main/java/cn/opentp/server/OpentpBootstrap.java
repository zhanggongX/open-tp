package cn.opentp.server;

import cn.opentp.server.command.CommandOptions;
import cn.opentp.server.configuration.Configuration;
import cn.opentp.server.report.ReportServer;
import cn.opentp.server.report.handler.ReportServerHandler;
import cn.opentp.server.rest.RestServer;
import cn.opentp.server.rest.controller.FaviconHttpHandler;
import cn.opentp.server.rest.controller.HttpHandler;
import cn.opentp.server.rest.controller.OpentpHttpHandler;
import cn.opentp.server.rocksdb.OpentpRocksDB;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class OpentpBootstrap {

    private static final Logger log = LoggerFactory.getLogger(OpentpBootstrap.class);

    private OpentpBootstrap() {

    }

    public static void main(String[] args) throws InterruptedException {

        CommandLineParser parser = new DefaultParser();
        Options options = CommandOptions.opentpOption();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException exp) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("opentp", options);
            return;
        }

        String exportPort = cmd.getOptionValue("ep");
        String serverPort = cmd.getOptionValue("sp");
        String httpPort = cmd.getOptionValue("hp");
        System.out.println(serverPort);
        System.out.println(exportPort);
        System.out.println(httpPort);

        ReportServer reportServer = new ReportServer();
        reportServer.start();

        RestServer restServer = new RestServer();
        restServer.start();

        Map<String, HttpHandler> endPoints = Configuration.configuration().endPoints();
        // 添加 handler
        endPoints.put("favicon.ico", new FaviconHttpHandler());
        endPoints.put("opentp", new OpentpHttpHandler());

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                log.debug("开始关闭 rocksDB");
                OpentpRocksDB.close();
                log.debug("完成关闭 rocksDB");

                log.debug("开始关闭 reportServer");
                reportServer.close();
                log.debug("开始关闭 reportServer");

                log.debug("开始关闭 restServer");
                restServer.close();
                log.debug("开始关闭 restServer");
            }
        }));
    }
}
