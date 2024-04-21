package opentp.client.spring.boot.starter.configuration;

import cn.opentp.client.OpentpClientBootstrap;
import cn.opentp.client.configuration.Configuration;
import jakarta.annotation.Resource;
import opentp.client.spring.boot.starter.annotation.EnableOpentp;
import opentp.client.spring.boot.starter.configuration.prop.OpentpProperties;
import opentp.client.spring.boot.starter.support.ServerAddressParser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.Comparator;
import java.util.List;

/**
 * 自动配置
 */
@AutoConfiguration
@ConditionalOnBean(annotation = EnableOpentp.class)
@EnableConfigurationProperties(OpentpProperties.class)
public class OpentpAutoConfiguration implements InitializingBean {

    @Resource
    private OpentpProperties opentpProperties;

    /**
     * new Bean -> set Bean -> all Aware -> beanPostProcessor(before) -> InitializingBean(afterPropertiesSet) -> init-method -> beanPostProcessor(after)
     * opentpProperties 已经注入
     * DisposableBean -> destroy-method
     *
     * @throws Exception InitializingBean 异常信息
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 添加配置信息
        List<InetSocketAddress> configInetSocketAddress = ServerAddressParser.parse(opentpProperties.getServers());
        Configuration configuration = Configuration.configuration();
        configuration.serverAddresses().addAll(configInetSocketAddress);

        configuration.nettyReconnectProperties().setInitialDelay(opentpProperties.getReconnect().getInitialDelay());
        configuration.nettyReconnectProperties().setPeriod(opentpProperties.getReconnect().getPeriod());

        configuration.threadPoolStateReportProperties().setInitialDelay(opentpProperties.getExport().getInitialDelay());
        configuration.threadPoolStateReportProperties().setPeriod(opentpProperties.getExport().getPeriod());

        if (opentpProperties.getAppKey() == null || opentpProperties.getAppKey().isEmpty()) {
            throw new IllegalArgumentException("请配置 opentp appKey");
        }
        if (opentpProperties.getAppSecret() == null || opentpProperties.getAppSecret().isEmpty()) {
            throw new IllegalArgumentException("请配置 opentp appSecret");
        }
        configuration.opentpClientProperties().setAppSecret(opentpProperties.getAppSecret());
        configuration.opentpClientProperties().setAppKey(opentpProperties.getAppKey());
    }

    @ConditionalOnMissingBean(OpentpClientBootstrap.class)
    @Bean
    public OpentpClientBootstrap opentpClientBootstrap() {
        return new OpentpClientBootstrap();
    }
}
