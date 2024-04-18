package opentp.client.spring.boot.starter.configuration;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.core.tp.ThreadPoolContext;
import cn.opentp.core.util.JSONUtils;
import opentp.client.spring.boot.starter.annotation.Opentp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public class OpentpSpringBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware, PriorityOrdered {

    private final static Logger log = LoggerFactory.getLogger(OpentpSpringBeanPostProcessor.class);

    private DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof ThreadPoolExecutor)) {
            return bean;
        }

        Opentp opentp = beanFactory.findAnnotationOnBean(beanName, Opentp.class);
        if (opentp == null) {
            return bean;
        }

        log.debug("OpentpThreadPoolScan find @Opentp bean name: {}, annotation value: {}", beanName, opentp.value());

        ThreadPoolContext threadPoolContext = new ThreadPoolContext((ThreadPoolExecutor) bean);
        Configuration configuration = Configuration.configuration();
        Map<String, ThreadPoolContext> threadPoolContextCache = configuration.threadPoolContextCache();

        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
            String hostname = addr.getHostName();
            log.info("当前主机信息：{}", JSONUtils.toJson(addr));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        threadPoolContextCache.put(opentp.value(), threadPoolContext);

        return bean;
    }
}
