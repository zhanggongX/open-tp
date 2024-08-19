package cn.opentp.server.network.restful.annotation;

import java.lang.annotation.*;

/**
 * http 路径变量
 *
 * @author zg
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathVariable {

    String value() default "";
}
