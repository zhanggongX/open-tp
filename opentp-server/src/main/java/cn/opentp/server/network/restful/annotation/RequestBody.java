package cn.opentp.server.network.restful.annotation;

import java.lang.annotation.*;

/**
 * JSON 格式的请求
 *
 * @author zg
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBody {

    String value() default "";
}
