package cn.opentp.core.auth;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * 认证
 */
public class ClientInfo implements Serializable {

    private String appKey;
    private String appSecret;
    private String host;
    private String instance;
    // 该客户端连接的服务端信息, 不参与 hash 等
    private ServerInfo serverInfo;

    public ClientInfo() {
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
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

    public ClientInfo(String appKey, String appSecret) {
        this();
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientInfo that = (ClientInfo) o;
        return Objects.equals(appKey, that.appKey) && Objects.equals(appSecret, that.appSecret) && Objects.equals(host, that.host) && Objects.equals(instance, that.instance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appKey, appSecret, host, instance);
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "appKey='" + appKey + '\'' +
                ", appSecret='" + appSecret + '\'' +
                ", host='" + host + '\'' +
                ", instance='" + instance + '\'' +
                ", serverInfo=" + serverInfo +
                '}';
    }

    public String clientInfoKey() {
        return appKey + ":" + host + ":" + instance;
    }
}
