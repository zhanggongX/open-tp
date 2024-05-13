package cn.opentp.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtil {

    private static final ObjectMapper instance = new ObjectMapper();

    public static ObjectMapper instance() {
        return instance;
    }
}
