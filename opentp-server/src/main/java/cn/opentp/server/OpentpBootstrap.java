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
import cn.opentp.server.transport.TransportServer;
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
            if(cmd.hasOption('h')){
                CommandOptions.printHelp(options);
                return;
            }
        } catch (ParseException exp) {
            CommandOptions.printHelp(options);
            return;
        }

        String reportPort = cmd.getOptionValue("rp");
        String httpPort = cmd.getOptionValue("hp");
        String transportPort = cmd.getOptionValue("tp");

        ReportServer reportServer = new ReportServer();
        reportServer.start(reportPort);

        RestServer restServer = new RestServer();
        restServer.start(httpPort);

        TransportServer transportServer = new TransportServer();
        transportServer.start(transportPort);

        Map<String, HttpHandler> endPoints = Configuration.configuration().endPoints();
        // 添加 handler
        endPoints.put("favicon.ico", new FaviconHttpHandler());
        endPoints.put("opentp", new OpentpHttpHandler());

        ShutdownHook shutdownHook = new ShutdownHook();
        shutdownHook.add(restServer);
        shutdownHook.add(reportServer);
        shutdownHook.add(transportServer);
        shutdownHook.add(OpentpRocksDB.rocksDB());

        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }
}
