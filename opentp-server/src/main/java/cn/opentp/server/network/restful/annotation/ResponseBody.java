package cn.opentp.server.network.restful.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 默认只支持 JSON 格式的响应
 * 所以 @ResponseBody 不需要了
 *
 * @author zg
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Deprecated
public @interface ResponseBody {

}
