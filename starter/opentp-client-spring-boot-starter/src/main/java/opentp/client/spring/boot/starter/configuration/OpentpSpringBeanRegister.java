package opentp.client.spring.boot.starter.configuration;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class OpentpSpringBeanRegister implements ImportBeanDefinitionRegistrar {

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean hadRegister = registry.containsBeanDefinition(OpentpThreadPoolScan.class.getSimpleName());
        if (!hadRegister) {
            registry.registerBeanDefinition(OpentpThreadPoolScan.class.getSimpleName(), new RootBeanDefinition(OpentpThreadPoolScan.class));
        }
    }
}
