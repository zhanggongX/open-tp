package opentp.client.spring.boot.starter.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * enable opentp mark
 */
@Configuration(proxyBeanMethods = false)
public class OpentpAutoConfigurationMarker {

    @Bean
    public Marker opentpAutoConfigurationMarker() {
        return new Marker();
    }

    public static class Marker {

    }
}
