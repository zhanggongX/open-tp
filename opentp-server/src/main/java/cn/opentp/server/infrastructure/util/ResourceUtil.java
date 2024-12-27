package cn.opentp.server.infrastructure.util;

import java.net.URL;

public class ResourceUtil {

    public static URL loadResource(ClassLoader classLoader, String fileName) {
        return classLoader.getResource(fileName);
    }
}
