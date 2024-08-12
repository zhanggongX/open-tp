package cn.opentp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * opentp 启动类
 */
public class Opentp {

    private static final Logger log = LoggerFactory.getLogger(Opentp.class);

    public static void main(String[] args) {
        log.info("opentp starting......");
        OpentpApp opentpApp = OpentpApp.instance();
        opentpApp.run(Opentp.class);
        log.info("opentp started......");
    }
}
