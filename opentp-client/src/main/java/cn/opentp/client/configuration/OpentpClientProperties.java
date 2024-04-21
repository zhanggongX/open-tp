package cn.opentp.client.configuration;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class OpentpClientProperties {

    private final static String UUID_SPLITTER = "-";

    private String appKey;
    private String appSecret;
    private String localhost;
    private String instance;

    public OpentpClientProperties() {
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
            this.localhost = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            Logger log = LoggerFactory.getLogger(this.getClass());
            log.warn("获取本机 IP 失败，使用空地址： ", e);
            this.localhost = Strings.EMPTY;
        }
        // 本机实例
        this.instance = UUID.randomUUID().toString().split(UUID_SPLITTER)[0];
    }

    public OpentpClientProperties(String appKey, String appSecret) {
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
}
