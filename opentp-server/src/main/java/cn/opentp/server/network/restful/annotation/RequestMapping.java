package cn.opentp.server.network.restful.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * http 请求路径
 * <p>
 * GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
 *
 * @author zg
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequestMapping {

    String value() default "";
}
