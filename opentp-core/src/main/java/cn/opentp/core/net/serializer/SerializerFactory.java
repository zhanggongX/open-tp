package cn.opentp.core.net.serializer;

import cn.opentp.core.net.serializer.kryo.KryoSerializer;

public class SerializerFactory {

    //由于Kryo是线程不安全的，所以我们这里使用ThreadLocal来解决线程安全问题
//    public static ThreadLocal<KryoSerializer> serializerThreadLocal = ThreadLocal.withInitial(KryoSerializer::new);

    public static Serializer serializer(byte type) {
        if (SerializerTypeEnum.Kryo.getType() == type) {
            return new KryoSerializer();
        }
        return new KryoSerializer();
    }
}
