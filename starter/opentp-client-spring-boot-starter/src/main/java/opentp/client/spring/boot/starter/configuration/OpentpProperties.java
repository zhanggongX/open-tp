
package opentp.client.spring.boot.starter.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = OpentpProperties.PREFIX)
public class OpentpProperties {

    public static final String PREFIX = "opentp";

    private String servers;

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }
}
