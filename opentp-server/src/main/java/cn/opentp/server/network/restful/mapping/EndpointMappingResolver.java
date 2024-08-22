package cn.opentp.server.network.restful.mapping;

import cn.opentp.server.network.restful.annotation.*;
import cn.opentp.server.network.restful.http.SupportHttpRequestType;
import cn.opentp.server.network.restful.register.MappingRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * endpoint mapping 解析器
 *
 * @author zg
 */
public class EndpointMappingResolver {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 扫描包路径下所有的class文件，并注册 mapping
     *
     * @param basePackage 包
     */
    public void registerMappings(String basePackage) throws IOException {
        Set<Class<?>> classes = scanClasses(basePackage);
        for (Class<?> cls : classes) {
            registerClass(cls);
        }
    }

    /**
     * 扫描包路径下所有的class文件
     *
     * @param packageName 包名
     * @return 扫描到的所有类
     */
    private Set<Class<?>> scanClasses(String packageName) throws IOException {
        Set<Class<?>> allClasses = new HashSet<>();

        String packageDir = packageName.replace('.', '/');
        Enumeration<URL> allUrls = this.getClass().getClassLoader().getResources(packageDir);
        while (allUrls.hasMoreElements()) {
            URL url = allUrls.nextElement();
            String protocol = url.getProtocol();

            if ("file".equals(protocol)) {
                // 如果是以文件的形式保存在服务器上
                String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                // 获取包的物理路径
                allClasses.addAll(scanClasses(filePath, packageName));
            } else if ("jar".equals(protocol)) {
                // 如果是jar包文件
                JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                allClasses.addAll(scanClasses(packageName, jar));
            }
        }

        return allClasses;
    }

    /**
     * 扫描包下的所有class文件
     *
     * @param path        文件路径
     * @param packageName 包名
     * @return 扫描到的所有类
     */
    private Set<Class<?>> scanClasses(String path, String packageName) {
        Set<Class<?>> classes = new HashSet<>();

        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptySet();
        }

        File[] files = dir.listFiles(filter -> filter.isDirectory() || filter.getName().endsWith("class"));
        if (files == null) {
            return Collections.emptySet();
        }

        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(scanClasses(packageName + "." + file.getName(), path + "/" + file.getName()));
                continue;
            }

            // 获取类名，去掉 ".class" 后缀
            String className = file.getName();
            className = packageName + "." + className.substring(0, className.length() - 6);

            // 加载类
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.getAnnotation(RestController.class) != null) {
                    classes.add(clazz);
                }
            } catch (ClassNotFoundException e) {
                log.warn("Class not found, {}", className);
            }
        }
        return classes;
    }

    /**
     * 扫描包路径下的所有class文件
     *
     * @param packageName 包名
     * @param jar         jar文件
     */
    private Set<Class<?>> scanClasses(String packageName, JarFile jar) {
        Set<Class<?>> classes = new HashSet<>();

        String packageDir = packageName.replace(".", "/");
        Enumeration<JarEntry> entry = jar.entries();
        while (entry.hasMoreElements()) {
            JarEntry jarEntry = entry.nextElement();

            String jarName = jarEntry.getName();
            if (jarName.charAt(0) == '/') {
                jarName = jarName.substring(1);
            }

            // 非指定包路径， 非class文件
            if (jarEntry.isDirectory() || !jarName.startsWith(packageDir) || !jarName.endsWith(".class")) {
                continue;
            }

            // 获取类名，去掉 ".class" 后缀
            String[] jarNameSplit = jarName.split("/");
            String className = packageName + "." + jarNameSplit[jarNameSplit.length - 1];
            if (className.endsWith(".class")) {
                className = className.substring(0, className.length() - 6);
            }

            // 加载类
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.getAnnotation(RestController.class) != null) {
                    classes.add(clazz);
                }
            } catch (ClassNotFoundException e) {
                log.error("ClassNotFoundException ：{}，：", className, e);
            }
        }

        return classes;
    }

    /**
     * 注册进 EndpointMapping
     *
     * @param clazz 类
     */
    private void registerClass(Class<?> clazz) {
        String className = clazz.getName();
        log.info("发现 RESTFul 类: {}", className);

        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            log.warn("RESTFul 类: {}, 没有配置 RequestMapping ！", className);
            return;
        }
        String classRequestUrl = requestMapping.value();

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            // mapping 注册器
            MappingRegister register = getMappingRegister(clazz, method);
            if (register == null) continue;

            // 执行 mapping 注册
            register.register(clazz, classRequestUrl, method);
        }
    }

    private MappingRegister getMappingRegister(Class<?> clazz, Method method) {
        MappingRegister register = null;
        if (method.getAnnotation(GetMapping.class) != null) {
            register = SupportHttpRequestType.GET.getMappingRegister();
        } else if (method.getAnnotation(PostMapping.class) != null) {
            register = SupportHttpRequestType.POST.getMappingRegister();
        } else if (method.getAnnotation(PutMapping.class) != null) {
            register = SupportHttpRequestType.PUT.getMappingRegister();
        } else if (method.getAnnotation(DeleteMapping.class) != null) {
            register = SupportHttpRequestType.DELETE.getMappingRegister();
        } else if (method.getAnnotation(PatchMapping.class) != null) {
            register = SupportHttpRequestType.PATCH.getMappingRegister();
        } else {
            log.warn("class : {}, method : {}, 未配置 Http 注解", clazz.getName(), method.getName());
        }
        return register;
    }
}
