package opentp.client.spring.boot.starter.annotation;

import opentp.client.spring.boot.starter.configuration.OpentpAutoConfigurationMarker;
import opentp.client.spring.boot.starter.configuration.OpentpSpringBeanImportSelector;
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
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({OpentpAutoConfigurationMarker.class, OpentpSpringBeanImportSelector.class})
public @interface EnableOpentp {
}
