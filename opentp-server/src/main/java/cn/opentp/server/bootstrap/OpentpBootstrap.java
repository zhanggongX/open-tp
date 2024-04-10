package cn.opentp.server.bootstrap;

import cn.opentp.server.http.NettyHttpServer;
import cn.opentp.server.http.handler.HttpHandler;
import cn.opentp.server.http.handler.OpentpHandler;
import cn.opentp.server.net.NettyServer;
import cn.opentp.server.tp.Configuration;
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

        Thread serverThread = NettyServer.start();
        Thread start = NettyHttpServer.start();
        Configuration configuration = Configuration.configuration();
        Map<String, HttpHandler> endPoints = configuration.getEndPoints();
        endPoints.put("opentp", new OpentpHandler());

        serverThread.join();
    }
}
