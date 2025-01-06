package cn.opentp.server.domain.connection;

import cn.opentp.server.domain.EventQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionImpl implements Connection {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String host;
    private String pid;
    private String appKey;
    private String appSecret;

    public ConnectionImpl() {
    }

    public ConnectionImpl(String host, String pid, String appKey, String appSecret) {
        this.host = host;
        this.pid = pid;
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

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public boolean handle(EventQueue eventQueue, ConnectCommand command) {
        log.debug("new connect host: {}, instance: {}, appKey: {}, appSecret: {}", command.getHost(), command.getPid(), command.getAppKey(), "...");
        return true;
    }

    @Override
    public String toString() {
        return "ConnectImpl{" +
                "host='" + host + '\'' +
                ", pid='" + pid + '\'' +
                ", appKey='" + appKey + '\'' +
                ", appSecret='" + appSecret + '\'' +
                '}';
    }
}
