package cn.opentp.core.net;

/**
 * 消息类型
 */
public enum OpentpMessageTypeEnum {

    /**
     * 心跳
     */
    HEART_PING((byte) 0x01),
    /**
     * 心跳相应
     */
    HEART_PONG((byte) 0x02),
    /**
     * 线程池信息上报
     */
    THREAD_POOL_EXPORT((byte) 0x03),
    /**
     * 线程池信息更新
     */
    THREAD_POOL_UPDATE((byte) 0x04);


    OpentpMessageTypeEnum(byte code) {
        this.code = code;
    }

    private final byte code;

    public static OpentpMessageTypeEnum parse(byte messageType) {
        for (OpentpMessageTypeEnum target : OpentpMessageTypeEnum.values()) {
            if (target.getCode() == messageType) {
                return target;
            }
        }
        return null;
    }

    public byte getCode() {
        return code;
    }
}
