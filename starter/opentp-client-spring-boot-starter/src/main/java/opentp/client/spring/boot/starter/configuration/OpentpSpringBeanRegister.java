package opentp.client.spring.boot.starter.configuration;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class OpentpSpringBeanRegister implements ImportBeanDefinitionRegistrar {

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean springBeanProcessorRegistered = registry.containsBeanDefinition(OpentpSpringBeanPostProcessor.class.getSimpleName());
        if (!springBeanProcessorRegistered) {
            // 去扫描 @opentp 的线程池
            registry.registerBeanDefinition(OpentpSpringBeanPostProcessor.class.getSimpleName(), new RootBeanDefinition(OpentpSpringBeanPostProcessor.class));
        }
    }
}
