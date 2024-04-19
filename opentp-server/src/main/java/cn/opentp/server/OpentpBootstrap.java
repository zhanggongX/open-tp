package cn.opentp.server;

import cn.opentp.server.command.CommandOptions;
import cn.opentp.server.configuration.Configuration;
import cn.opentp.server.http.NettyHttpBootstrap;
import cn.opentp.server.http.handler.FaviconHttpHandler;
import cn.opentp.server.http.handler.HttpHandler;
import cn.opentp.server.http.handler.OpentpHttpHandler;
import cn.opentp.server.net.NettyBootstrap;
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

        Thread nettyBootstrap = NettyBootstrap.start();
        Thread nettyHttpBootstrap = NettyHttpBootstrap.start();

        Map<String, HttpHandler> endPoints = Configuration.configuration().endPoints();
        // 添加 handler
        endPoints.put("favicon.ico", new FaviconHttpHandler());
        endPoints.put("opentp", new OpentpHttpHandler());

        nettyBootstrap.join();
    }
}
