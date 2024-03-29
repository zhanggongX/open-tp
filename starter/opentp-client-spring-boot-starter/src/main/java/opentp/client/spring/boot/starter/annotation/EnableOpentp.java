package opentp.client.spring.boot.starter.annotation;

import opentp.client.spring.boot.starter.configuration.OpentpAutoConfigurationMarker;
import opentp.client.spring.boot.starter.configuration.OpentpSpringBeanRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 1, OpentpAutoConfigurationSelector
 * 2, OpentpSpringBeanRegister
 * 3, before init --
 * 4, all ThreadPoolExecutor
 * 5, after init --
 * 6, OpentpAutoConfigurationMarker
 * 7, OpentpAutoConfiguration
 * <p>
 * 如果你需要根据条件动态地选择性地导入配置类，则使用 DeferredImportSelector 更合适；
 * 如果你需要在 Spring 容器准备阶段动态注册额外的 Bean 定义，则使用 ImportBeanDefinitionRegistrar 更合适；
 * 如果只是需要简单地注册一个 Bean，则直接在 spring.factories 文件中声明即可。
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
//@Import({OpentpAutoConfigurationMarker.class, OpentpSpringBeanImportSelector.class, OpentpSpringBeanRegister.class})
@Import({OpentpAutoConfigurationMarker.class, OpentpSpringBeanRegister.class})
public @interface EnableOpentp {
}
