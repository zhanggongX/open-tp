package opentp.client.spring.boot.starter.annotation;

import java.lang.annotation.*;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Opentp {

    String value() default "";
}
