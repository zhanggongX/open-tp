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

public class JSONUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //  null 不参加序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * 解析json
     *
     * @param content   字符串
     * @param valueType 类型
     * @return T
     * @author zg
     * @date 2020-10-27
     */
    public static <T> T fromJson(String content, Class<T> valueType) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            return objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("jackson read value exception, content = " + content, e);
        }
    }

    public static <T> T fromJsonRefer(String content, Class<T> clazz) {
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

    public static <T> List<T> fromJsonList(String content, Class<T> clazz) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            return objectMapper.readValue(content, getListType(clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("jackson read list value exception, content = " + content + ", Class = " + clazz.getName(), e);
        }
    }

    public static <T> Set<T> fromJsonSet(String content, Class<T> clazz) {
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
     * 生成json
     *
     * @param object 对象
     * @return String
     * @author zg
     * @date 2020-10-27
     */
    public static String toJson(Object object) {
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
     * 获取node值
     *
     * @return String
     * @author zg
     * @date 2020-10-27
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
     * 获取node
     *
     * @return String
     * @author zg
     * @date 2020-10-27
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
     *
     * @param elementClasses 泛型类型
     * @return JavaType
     * @author zg
     * @date 2020-12-17
     */
    private static JavaType getListType(Class<?>... elementClasses) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper.getTypeFactory().constructParametricType(List.class, elementClasses);
    }

    private static JavaType getSetType(Class<?>... elementClasses) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper.getTypeFactory().constructParametricType(Set.class, elementClasses);
    }
}