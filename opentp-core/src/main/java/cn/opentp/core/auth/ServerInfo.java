package cn.opentp.core.auth;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.*;
import java.util.Enumeration;

public class ServerInfo {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String host;
    private int port;
    private final String pid;

    public ServerInfo() {
        this.host = getLocalHostIp();
        // 本机实例，客户端的 pid
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        this.pid = runtimeMXBean.getName().split("@")[0];
    }

    public ServerInfo(String host, String instance) {
        this.host = host;
        this.pid = instance;
    }

    private String getLocalHostIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                // 如果回环地址，虚拟网卡，未启动的网卡，则跳过
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress ip = inetAddresses.nextElement();
                        if (ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
            log.warn("获取本机 IP 失败！");
            throw new RuntimeException();
        } catch (SocketException e) {
            log.warn("获取本机 IP 失败： ", e);
            throw new RuntimeException(e);
        }
    }

    public String getHost() {
        return host;
    }

    public String getPid() {
        return pid;
    }

    @Override
    public String toString() {
        return host + "@" + pid;
    }
}
