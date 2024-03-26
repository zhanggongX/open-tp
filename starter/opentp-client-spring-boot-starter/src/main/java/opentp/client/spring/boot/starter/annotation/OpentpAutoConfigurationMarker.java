package opentp.client.spring.boot.starter.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class OpentpAutoConfigurationMarker {

    @Bean
    public Marker opentpAutoConfigurationBean() {
        return new Marker();
    }

    public static class Marker {

    }
}
