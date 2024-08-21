package cn.opentp.server.network.restful.mapping;

import cn.opentp.server.network.restful.annotation.PathVariable;
import cn.opentp.server.network.restful.annotation.RequestBody;
import cn.opentp.server.network.restful.annotation.RequestHeader;
import io.netty.handler.codec.http.FullHttpRequest;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 请求映射类
 *
 * @author zg
 */
public final class EndpointMapping {

    /**
     * 类+方法的完整请求路径
     */
    private String requestUrl;

    private Class<?> clazz;

//    private String className;

    private Method method;

//    private String methodName;

    private List<EndpointMappingParam> params = new ArrayList<>();

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

//    public String getClassName() {
//        return className;
//    }
//
//    public void setClassName(String className) {
//        this.className = className;
//    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

//    public String getMethodName() {
//        return methodName;
//    }
//
//    public void setMethodName(String methodName) {
//        this.methodName = methodName;
//    }

    public List<EndpointMappingParam> getParams() {
        return params;
    }

    public void setParams(List<EndpointMappingParam> params) {
        this.params = params;
    }
}
