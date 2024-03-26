package opentp.client.spring.boot.starter.configuration;

import opentp.client.spring.boot.starter.annotation.EnableOpentp;
import opentp.client.spring.boot.starter.annotation.OpentpAutoConfigurationMarker;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(OpentpAutoConfigurationMarker.Marker.class)
public class OpentpAutoConfiguration implements InitializingBean, BeanNameAware {

    private String beanName;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(1111);
    }

    @Override
    public void setBeanName(String name) {
        beanName = "hhhhh";
    }
}
