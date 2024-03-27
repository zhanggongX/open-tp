package opentp.client.spring.boot.starter.annotation;

import opentp.client.spring.boot.starter.configuration.OpentpAutoConfigurationMarker;
import opentp.client.spring.boot.starter.configuration.OpentpAutoConfigurationSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({OpentpAutoConfigurationMarker.class, OpentpAutoConfigurationSelector.class})
public @interface EnableOpentp {
}
