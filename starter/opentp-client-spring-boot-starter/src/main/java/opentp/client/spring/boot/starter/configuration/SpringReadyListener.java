package opentp.client.spring.boot.starter.configuration;

import cn.opentp.client.core.OpentpClientBootstrap;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;

public class SpringReadyListener implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {


    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        OpentpClientBootstrap opentpClientBootstrap = applicationContext.getBean(OpentpClientBootstrap.class);
        opentpClientBootstrap.start();
    }
}
