package cn.opentp.server.domain.connect;

import cn.opentp.server.domain.DomainCommand;

/**
 * 连接命令
 */
public class ConnectCommand implements DomainCommand {

    private String host;
    private String pid;
    private String appKey;
    private String appSecret;

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
}
