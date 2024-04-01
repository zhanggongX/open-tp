package cn.opentp.server.bootstrap;

import org.apache.commons.cli.*;

public class CommandOptions {

    public static Options opentpOption() {

        Options options = new Options();
        options.addOption("a", "all", false, "do not hide entries starting with .");
        options.addOption("A", "almost-all", false, "do not list implied . and ..");
        options.addOption("b", "escape", false, "print octal escapes for non-graphic "
                + "characters");
        options.addOption(Option.builder("SIZE").longOpt("block-size")
                .desc("use SIZE-byte blocks")
                .hasArg()
                .build());
        options.addOption("B", "ignore-backups", false, "do not list implied entries "
                + "ending with ~");
        options.addOption("c", false, "with -lt: sort by, and show, ctime (time of last "
                + "modification of file status information) with "
                + "-l:show ctime and sort by name otherwise: sort "
                + "by ctime");
        options.addOption("C", false, "list entries by columns");
        return options;
    }
}
