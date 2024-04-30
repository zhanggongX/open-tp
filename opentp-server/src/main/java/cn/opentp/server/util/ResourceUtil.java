package cn.opentp.server.util;

import java.net.URL;

public class ResourceUtil {

    public static URL loadResource(ClassLoader classLoader, String fileName) {
        return classLoader.getResource(fileName);
    }
}
