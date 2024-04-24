package cn.opentp.server.command;

import org.apache.commons.cli.*;

public class CommandOptions {

    public static Options opentpOption() {

        Options options = new Options();
        options.addOption("ep", "export-port", true, "client export use port");
        options.addOption("sp", "server-port", true, "server sync info port");
        options.addOption("hp", "http-port", true, "http rest api port");
        return options;
    }
}
