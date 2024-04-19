package opentp.client.spring.boot.starter.configuration;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.core.tp.ThreadPoolContext;
import opentp.client.spring.boot.starter.annotation.Opentp;
import opentp.client.spring.boot.starter.exception.OpentpDupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

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

    /**
     * 在 spring bean 初始化回调
     * 如 InitializingBean 的 afterPropertiesSet 方法或者自定义的 init-method 之前被调用
     * 也就是说，这个方法会在bean的属性已经设置完毕，但还未进行初始化时被调用。
     *
     * @param bean     bean 对象
     * @param beanName bean name
     * @return bean
     * @throws BeansException 异常信息
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 在 spring bean 初始化后回调
     * 比如 InitializingBean 的 afterPropertiesSet 或者自定义的初始化方法之后被调用
     * 这个时候，bean的属性值已经被填充完毕。返回的bean实例可能是原始bean的一个包装。
     *
     * @param bean     bean 对象
     * @param beanName bean name
     * @return bean
     * @throws BeansException 异常信息
     */
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

        if (threadPoolContextCache.containsKey(opentp.value())) {
            throw new OpentpDupException();
        }

        threadPoolContextCache.put(opentp.value(), threadPoolContext);
        return bean;
    }
}
