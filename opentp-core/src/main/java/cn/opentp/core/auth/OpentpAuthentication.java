package cn.opentp.core.auth;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * 认证
 */
public class OpentpAuthentication implements Serializable {

    private final static String UUID_SPLITTER = "-";

    private String appKey;
    private String appSecret;
    private String host;
    private String instance;

    public OpentpAuthentication() {
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
            this.host = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            Logger log = LoggerFactory.getLogger(this.getClass());
            log.warn("获取本机 IP 失败，使用空地址： ", e);
            this.host = Strings.EMPTY;
        }
        // 本机实例
        this.instance = UUID.randomUUID().toString().split(UUID_SPLITTER)[0];
    }

    public OpentpAuthentication(String appKey, String appSecret) {
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
}
