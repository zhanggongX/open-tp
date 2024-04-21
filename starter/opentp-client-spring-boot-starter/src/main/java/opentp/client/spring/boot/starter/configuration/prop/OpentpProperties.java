
package opentp.client.spring.boot.starter.configuration.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = OpentpProperties.PREFIX)
public class OpentpProperties {

    public static final String PREFIX = "opentp";

    private String servers;
    private ReconnectProperties reconnect;
    private ExportProperties export;
    private String name;

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
