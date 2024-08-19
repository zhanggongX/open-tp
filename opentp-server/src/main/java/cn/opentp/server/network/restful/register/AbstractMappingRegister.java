package cn.opentp.server.network.restful.register;

import cn.opentp.server.network.restful.SupportHttpRequestType;
import cn.opentp.server.network.restful.annotation.PathVariable;
import cn.opentp.server.network.restful.annotation.RequestBody;
import cn.opentp.server.network.restful.annotation.RequestHeader;
import cn.opentp.server.network.restful.annotation.UrlEncodedForm;
import cn.opentp.server.network.restful.mapping.EndPointMappingParamType;
import cn.opentp.server.network.restful.mapping.EndpointMapping;
import cn.opentp.server.network.restful.mapping.EndpointMappingParam;
import io.netty.handler.codec.http.FullHttpRequest;
import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * mapping 注册
 *
 * @author zg
 */
public abstract class AbstractMappingRegister implements MappingRegister {

    private final Logger log = LoggerFactory.getLogger(AbstractMappingRegister.class);

    @Override
    public void register(Class<?> clazz, String classRequestUrl, Method method) {

        String methodRequestUrl = resolveMethodRequestUrl(method);
        String completeRequestUrl = buildCompleteRequestUrl(classRequestUrl, methodRequestUrl);
        if (completeRequestUrl.isEmpty()) {
            return;
        }

        EndpointMapping endpointMapping = new EndpointMapping();
        endpointMapping.setUrl(completeRequestUrl);
        endpointMapping.setClassName(clazz.getName());
        endpointMapping.setClassMethod(method.getName());
//        String httpMethod = resolveHttpMethod();
//        if (httpMethod == null) {
//            log.error("请求{}-{}不支持的请求类型，目前只支持：{}", clazz.getName(), method.getName(), SupportHttpRequestType.supportedHttpMethods());
//            throw new IllegalCallerException(clazz.getName() + "/" + method.getName());
//        }

        // 得到参数
        Parameter[] parameters = method.getParameters();
        if (parameters.length > 0) {
            // 得到所有参数名
            String[] paramNames = resolveMethodParamNames(clazz, method);
            for (int i = 0; i < parameters.length; i++) {
                EndpointMappingParam endpointMappingParam = new EndpointMappingParam();
                endpointMappingParam.setDataType(parameters[i].getType());

                if (parameters[i].getType().equals(FullHttpRequest.class)) {
                    endpointMappingParam.setName(paramNames[i]);
                    endpointMappingParam.setType(EndPointMappingParamType.HTTP_REQUEST);
                    endpointMapping.getParams().add(endpointMappingParam);
                    continue;
                }

                if (parameters[i].getAnnotation(RequestHeader.class) != null) {
                    RequestHeader requestHeader = parameters[i].getAnnotation(RequestHeader.class);
                    endpointMappingParam.setName((requestHeader.value() != null && !requestHeader.value().trim().isEmpty()) ?
                            requestHeader.value().trim() : paramNames[i]);
                    endpointMappingParam.setRequired(requestHeader.required());
                    endpointMappingParam.setType(EndPointMappingParamType.REQUEST_HEADER);
                    endpointMapping.getParams().add(endpointMappingParam);
                    continue;
                }

                if (parameters[i].getAnnotation(PathVariable.class) != null) {
                    PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
                    endpointMappingParam.setName((pathVariable.value() != null && !pathVariable.value().trim().isEmpty()) ?
                            pathVariable.value().trim() : paramNames[i]);
                    endpointMappingParam.setType(EndPointMappingParamType.PATH_VARIABLE);
                    endpointMapping.getParams().add(endpointMappingParam);
                    continue;
                }

                if (parameters[i].getAnnotation(RequestBody.class) != null) {
                    endpointMappingParam.setName(paramNames[i]);
                    endpointMappingParam.setType(EndPointMappingParamType.REQUEST_BODY);
                    endpointMapping.getParams().add(endpointMappingParam);
                    continue;
                }

                endpointMappingParam.setName(paramNames[i]);
                endpointMappingParam.setType(EndPointMappingParamType.REQUEST_PARAM);
                endpointMapping.getParams().add(endpointMappingParam);
            }
        }
        registerMapping(completeRequestUrl, endpointMapping);
    }

    /**
     * 解析方法请求URL
     *
     * @param method 方法
     * @return 方法请求URL
     */
    abstract String resolveMethodRequestUrl(Method method);

    /**
     * 获取的完整的请求路径
     *
     * @param classRequestUrl  类上的请求URL
     * @param methodRequestUrl 方法上的请求URL
     * @return 完整的请求路径
     */
    private String buildCompleteRequestUrl(String classRequestUrl, String methodRequestUrl) {
        StringBuilder completeRequestUrl = new StringBuilder();

        completeRequestUrl.append((classRequestUrl != null && !classRequestUrl.trim().isEmpty()) ? classRequestUrl.trim() : "");

        if (methodRequestUrl != null && !methodRequestUrl.trim().isEmpty()) {
            methodRequestUrl = methodRequestUrl.trim();
            // 如果方法上的请求URL不是以/开头，并且类上的请求URL不是以/结尾，则添加/
            if (!methodRequestUrl.startsWith("/") && !completeRequestUrl.toString().endsWith("/")) {
                methodRequestUrl = "/" + methodRequestUrl;
            }

            completeRequestUrl.append(methodRequestUrl);
        }
        return completeRequestUrl.toString();
    }

    /**
     * 获取请求方法
     *
     * @return 请求方法
     */
//    abstract String resolveHttpMethod();

    /**
     * 注册 mapping
     *
     * @param completeRequestUrl 完整的请求路径
     * @param endpointMapping    mapping
     */
    abstract void registerMapping(String completeRequestUrl, EndpointMapping endpointMapping);

    /**
     * 得到方法的所有参数名称
     *
     * @param clazz  endpoint 类
     * @param method endpoint 方法
     * @return 所有的参数
     */
    private String[] resolveMethodParamNames(Class<?> clazz, final Method method) {
        final Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            return new String[0];
        }

        final Type[] types = new Type[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            types[i] = Type.getType(parameterTypes[i]);
        }

        final String[] paramNames = new String[parameterTypes.length];

        String className = clazz.getName();
        className = className.substring(className.lastIndexOf(".") + 1) + ".class";
        InputStream is = clazz.getResourceAsStream(className);
        try {
            assert is != null;
            ClassReader classReader = new ClassReader(is);
            classReader.accept(new ClassVisitor(Opcodes.ASM4) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    // 只处理指定的方法
                    Type[] argumentTypes = Type.getArgumentTypes(desc);
                    if (!method.getName().equals(name) || !Arrays.equals(argumentTypes, types)) {
                        return null;
                    }
                    return new MethodVisitor(Opcodes.ASM4) {
                        @Override
                        public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
                            if (Modifier.isStatic(method.getModifiers())) {
                                paramNames[index] = name;
                            } else if (index > 0 && index <= paramNames.length) {
                                // 非静态方法第一个参数是对象this
                                paramNames[index - 1] = name;
                            }
                        }
                    };

                }
            }, 0);
        } catch (IOException e) {
            // 记录异常堆栈信息
            log.error("发生异常: ", e);
        }
        return paramNames;
    }
}