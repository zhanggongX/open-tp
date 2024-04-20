package cn.opentp.core.net.serializer;

import cn.opentp.core.net.serializer.kryo.KryoSerializer;

public class SerializerFactory {

    public static Serializer serializer(byte type) {
        if (SerializerTypeEnum.Kryo.getType() == type) {
            return new KryoSerializer();
        }
        return new KryoSerializer();
    }
}
