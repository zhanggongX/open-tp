package cn.opentp.server.domain.connection;

import cn.opentp.server.domain.EventQueue;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ConnectionImpl implements Connection {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String host;
    private String pid;
    private String appKey;
    private String appSecret;
    private Channel channel;

    public ConnectionImpl() {
    }

    public ConnectionImpl(String host, String pid) {
        this.host = host;
        this.pid = pid;
    }

    public ConnectionImpl(String host, String pid, String appKey, String appSecret, Channel channel) {
        this.host = host;
        this.pid = pid;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.channel = channel;
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

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean handle(EventQueue eventQueue, ConnectCommand command) {
        log.debug("new connect host: {}, instance: {}, appKey: {}, appSecret: {}", command.getHost(), command.getPid(), command.getAppKey(), "...");
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionImpl that = (ConnectionImpl) o;
        return Objects.equals(host, that.host) && Objects.equals(pid, that.pid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, pid);
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
