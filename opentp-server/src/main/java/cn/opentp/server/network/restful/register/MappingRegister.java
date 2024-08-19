package cn.opentp.server.network.restful.register;

import java.lang.reflect.Method;

/**
 * mapping 注册
 *
 * @author zg
 */
public interface MappingRegister {

    /**
     * mapping 注册
     *
     * @param clazz           RESTFul 类
     * @param classRequestUrl 类上的请求路径
     * @param method          具体方法
     */
    void register(Class<?> clazz, String classRequestUrl, Method method);
}
