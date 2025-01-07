package cn.opentp.server.domain.connection;

import cn.opentp.server.domain.DomainCommand;
import io.netty.channel.Channel;

/**
 * 连接命令
 */
public class ConnectCommand implements DomainCommand {

    private String host;
    private String pid;
    private String appKey;
    private String appSecret;
    private Channel channel;

    public ConnectCommand() {
    }

    public ConnectCommand(String host, String pid) {
        this.host = host;
        this.pid = pid;
    }

    public ConnectCommand(String host, String pid, String appKey, String appSecret, Channel channel) {
        this.host = host;
        this.pid = pid;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.channel = channel;
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

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
