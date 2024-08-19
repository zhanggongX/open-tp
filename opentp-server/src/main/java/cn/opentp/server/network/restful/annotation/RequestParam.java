package cn.opentp.server.network.restful.annotation;

import java.lang.annotation.*;

/**
 * http 请求参数
 * 
 * @author zg
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
    
    String value() default "";
    
    boolean required() default true;
}
