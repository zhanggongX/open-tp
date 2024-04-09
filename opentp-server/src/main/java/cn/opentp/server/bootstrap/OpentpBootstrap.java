package cn.opentp.server.bootstrap;

import cn.opentp.server.net.NettyHttpServer;
import cn.opentp.server.net.NettyServer;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        serverThread.join();
    }
}
