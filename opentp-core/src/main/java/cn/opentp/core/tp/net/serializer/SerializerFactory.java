package cn.opentp.core.tp.net.serializer;

/**
 * 序列化工具类工厂实现
 * @author stone
 * @date 2019/7/31 11:21
 */
public class SerializerFactory {

    public static Serializer getSerializer(Class<?> cls) {
        return new KryoSerializer(cls);
    }
}