package cn.opentp.server.network.restful.annotation;

import java.lang.annotation.*;

/**
 * http 请求头
 * 
 * @author zg
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestHeader {

    String value() default "";
    
    boolean required() default true;
}
