package opentp.client.spring.boot.starter.configuration;

import opentp.client.spring.boot.starter.annotation.EnableOpentp;
import opentp.client.spring.boot.starter.exception.ServerAddrUnDefineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 自动配置
 */
@Configuration
@ConditionalOnBean(annotation = EnableOpentp.class)
@EnableConfigurationProperties(OpentpProperties.class)
public class OpentpAutoConfiguration implements EnvironmentAware {


    private OpentpProperties properties;
    private Environment environment;

    @Autowired
    public OpentpAutoConfiguration(OpentpProperties opentpProperties) {
        this.properties = opentpProperties;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @ConditionalOnMissingBean(cn.opentp.client.configuration.Configuration.class)
    @Bean
    public cn.opentp.client.configuration.Configuration opentpConfiguration() {
        String property = environment.getProperty("opentp.servers");
        // 配置地址信息
        List<InetSocketAddress> inetSocketAddresses = cn.opentp.client.configuration.Configuration.configuration().serverAddresses();
        String servers = properties.getServers();
        String[] serverList = servers.split(cn.opentp.client.configuration.Configuration.SERVER_SPLITTER);
        if (serverList.length == 0) {
            throw new ServerAddrUnDefineException("需要先配置服务端地址");
        }
        for (String server : serverList) {
            String[] serverAndPort = server.split(cn.opentp.client.configuration.Configuration.SERVER_PORT_SPLITTER);
            if (serverAndPort.length <= 1) {
                inetSocketAddresses.add(new InetSocketAddress(serverAndPort[0], cn.opentp.client.configuration.Configuration.DEFAULT_PORT));
            } else {
                inetSocketAddresses.add(new InetSocketAddress(serverAndPort[0], Integer.parseInt(serverAndPort[1])));
            }
        }
        return cn.opentp.client.configuration.Configuration.configuration();
    }
}
