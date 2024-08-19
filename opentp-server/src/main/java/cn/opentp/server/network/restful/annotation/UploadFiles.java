package cn.opentp.server.network.restful.annotation;

import java.lang.annotation.*;

/**
 * 多文件上传，暂不支持
 *
 * @author zg
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface UploadFiles {
}
