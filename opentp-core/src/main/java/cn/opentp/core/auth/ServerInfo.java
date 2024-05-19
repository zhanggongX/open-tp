package cn.opentp.core.auth;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerInfo {

    private String host;
    private final String instance;

    public ServerInfo() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            this.host = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            Logger log = LoggerFactory.getLogger(this.getClass());
            log.warn("获取本机 IP 失败，使用空地址： ", e);
            this.host = Strings.EMPTY;
        }
        // 本机实例，客户端的 pid
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        this.instance = runtimeMXBean.getName().split("@")[0];
    }

    public ServerInfo(String host, String instance) {
        this.host = host;
        this.instance = instance;
    }

    public String getHost() {
        return host;
    }

    public String getInstance() {
        return instance;
    }
}
