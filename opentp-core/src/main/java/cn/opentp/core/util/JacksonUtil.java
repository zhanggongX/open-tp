package cn.opentp.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

/**
 * 只处理简单对象
 */
public class JacksonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //  null 不参加序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * 解析json
     */
    public static <T> T parseJson(String content, Class<T> valueType) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            return objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("jackson read value exception, content = " + content, e);
        }
    }

    /**
     * 解析json
     */
    public static <T> T parseJsonRefer(String content, Class<T> clazz) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            return objectMapper.readValue(content, new TypeReference<T>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("jackson read value exception, content = " + content + ", Class = " + clazz.getName(), e);
        }
    }

    /**
     * 解析json -> list
     */
    public static <T> List<T> parseJsonList(String content, Class<T> clazz) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            return objectMapper.readValue(content, getListType(clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("jackson read list value exception, content = " + content + ", Class = " + clazz.getName(), e);
        }
    }

    /**
     * 解析json -> set
     */
    public static <T> Set<T> parseJsonSet(String content, Class<T> clazz) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            return objectMapper.readValue(content, getSetType(clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("jackson read set value exception, content = " + content + ", Class = " + clazz.getName(), e);
        }
    }

    /**
     * 生成json string
     */
    public static String toJSONString(Object object) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("jackson write value exception, content = " + object.toString(), e);
        }
    }

    /**
     * 获取 jsonNode 值
     */
    public static String getNodeVal(String content, String key) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            return objectMapper.readTree(content).get(key).asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("jackson read tree node exception, content = " + content + ", key = " + key, e);
        }
    }

    /**
     * 获取 jsonNode
     */
    public static JsonNode getNode(String content) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            return objectMapper.readTree(content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("jackson read tree exception, content = " + content, e);
        }
    }

    /**
     * 获取泛型的 List Type
     */
    private static JavaType getListType(Class<?>... elementClasses) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper.getTypeFactory().constructParametricType(List.class, elementClasses);
    }

    /**
     * 获取泛型的 Set Type
     */
    private static JavaType getSetType(Class<?>... elementClasses) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper.getTypeFactory().constructParametricType(Set.class, elementClasses);
    }
}