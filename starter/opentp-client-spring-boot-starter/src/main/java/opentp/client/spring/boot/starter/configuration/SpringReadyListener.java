package opentp.client.spring.boot.starter.configuration;

import cn.opentp.client.bootstrap.OpentpClientBootstrap;
import opentp.client.spring.boot.starter.annotation.EnableOpentp;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

import java.util.Map;

public class SpringReadyListener implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Map<String, Object> enableBeans = applicationContext.getBeansWithAnnotation(EnableOpentp.class);
        if (enableBeans.isEmpty()) {
            return;
        }
        OpentpClientBootstrap opentpClientBootstrap = applicationContext.getBean(OpentpClientBootstrap.class);
        opentpClientBootstrap.start();
    }
}
