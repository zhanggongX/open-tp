
package opentp.client.spring.boot.starter.configuration.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = OpentpProperties.PREFIX)
public class OpentpProperties {

    public static final String PREFIX = "opentp";

    private String servers;
    private ReconnectProperties reconnect;
    private ExportProperties export;
    private String appKey;
    private String appSecret;

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }

    public ReconnectProperties getReconnect() {
        return reconnect;
    }

    public void setReconnect(ReconnectProperties reconnect) {
        this.reconnect = reconnect;
    }

    public ExportProperties getExport() {
        return export;
    }

    public void setExport(ExportProperties export) {
        this.export = export;
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
