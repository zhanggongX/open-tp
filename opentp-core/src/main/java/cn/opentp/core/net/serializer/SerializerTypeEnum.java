package cn.opentp.core.net.serializer;

public enum SerializerTypeEnum {

    Kryo((byte) 0x01);

    SerializerTypeEnum(byte type) {
        this.type = type;
    }

    private final byte type;

    public byte getType() {
        return type;
    }
}
