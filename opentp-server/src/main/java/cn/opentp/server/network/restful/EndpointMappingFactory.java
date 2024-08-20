package cn.opentp.server.network.restful;

import cn.opentp.server.network.restful.annotation.*;
import cn.opentp.server.network.restful.register.EndpointMappingRegisterHolder;
import cn.opentp.server.network.restful.register.MappingRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * endpoint mapping 解析
 *
 * @author zg
 */
public class EndpointMappingFactory {

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
            // 暂不支持其它格式的文件类型
            if ("file".equals(protocol)) {
                // 如果是以文件的形式保存在服务器上
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
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
     * @param path
     * @param packageName
     */
    private Set<Class<?>> scanClasses(String path, String packageName) {
        Set<Class<?>> classes = new HashSet<>();

        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptySet();
        }
        File[] files = dir.listFiles(filter -> filter.isDirectory() || filter.getName().endsWith("class"));
        for (File f : files) {
            if (f.isDirectory()) {
                classes.addAll(scanClasses(packageName + "." + f.getName(), path + "/" + f.getName()));
                continue;
            }

            // 获取类名，去掉 ".class" 后缀
            String className = f.getName();
            className = packageName + "." + className.substring(0, className.length() - 6);

            // 加载类
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                log.error("Class not found, {}", className);
            }
            if (clazz != null && clazz.getAnnotation(RestController.class) != null) {
                if (clazz != null) {
                    classes.add(clazz);
                }
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
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
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
        log.info("find RESTFul class: {}", className);

        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            return;
        }
        String url = requestMapping.value();

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {

            MappingRegister register = null;
            // 遍历所有method，生成ControllerMapping并注册。
            if (method.getAnnotation(GetMapping.class) != null) {
                register = EndpointMappingRegisterHolder.MAPPING_REGISTER_MAP.get(SupportHttpRequestType.GET);
            } else if (method.getAnnotation(PostMapping.class) != null) {
                register = EndpointMappingRegisterHolder.MAPPING_REGISTER_MAP.get(SupportHttpRequestType.POST);
            } else if (method.getAnnotation(PutMapping.class) != null) {
                register = EndpointMappingRegisterHolder.MAPPING_REGISTER_MAP.get(SupportHttpRequestType.PUT);
            } else if (method.getAnnotation(DeleteMapping.class) != null) {
                register = EndpointMappingRegisterHolder.MAPPING_REGISTER_MAP.get(SupportHttpRequestType.DELETE);
            } else if (method.getAnnotation(PatchMapping.class) != null) {
                register = EndpointMappingRegisterHolder.MAPPING_REGISTER_MAP.get(SupportHttpRequestType.PATCH);
            }

            if (register != null) {
                register.register(clazz, url, method);
            }
        }
    }
}
