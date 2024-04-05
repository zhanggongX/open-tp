package opentp.client.spring.boot.starter.configuration;

import cn.opentp.client.bootstrap.OpentpClientBootstrap;
import cn.opentp.client.context.OpentpContext;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class OpentpSpringBeanRegister implements ImportBeanDefinitionRegistrar {

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean hadRegister = registry.containsBeanDefinition(OpentpSpringBeanPostProcessor.class.getSimpleName());
        if (!hadRegister) {
            // opentp context
//            registry.registerBeanDefinition(OpentpContext.class.getSimpleName(), new RootBeanDefinition(OpentpContext.class));
            // opentp start
            registry.registerBeanDefinition(OpentpClientBootstrap.class.getSimpleName(), new RootBeanDefinition(OpentpClientBootstrap.class));
            // spring scan @opentp
            registry.registerBeanDefinition(OpentpSpringBeanPostProcessor.class.getSimpleName(), new RootBeanDefinition(OpentpSpringBeanPostProcessor.class));
        }
    }
}
