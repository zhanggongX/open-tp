package cn.opentp.server.command;

import org.apache.commons.cli.*;

public class CommandOptions {

    public static Options opentpOption() {

        Options options = new Options();
        options.addOption("rp", "report-port", true, "client report use port");
        options.addOption("hp", "http-port", true, "http rest api port");
        options.addOption("tp", "transport-port", true, "opentp sync info port");
        options.addOption("h", "help", false, "print out usages");

        return options;
    }

    public static void printHelp(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("opentp", options);
    }
}
