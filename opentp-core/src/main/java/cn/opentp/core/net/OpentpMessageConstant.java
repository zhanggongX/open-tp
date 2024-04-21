package cn.opentp.core.net;

/**
 * 消息协议常量
 */
public class OpentpMessageConstant {

    /**
     * OTP
     */
    public static final byte[] MAGIC = new byte[]{(byte) 'O', (byte) 'T', (byte) 'P'};
    /**
     * major version， sub version, modify version
     * 1.0.1
     */
    public static final byte[] VERSION = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x01};

    /**
     * 魔数 + 版本 + Int(length) + Byte(messageType) + Byte(serializerType) + Long(traceId) + Int(licenseKeyLength)
     */
    public static final int MESSAGE_HEAD_LENGTH = MAGIC.length + VERSION.length + 4 + 1 + 1 + 8 + 4;


    /**
     * 8MB
     */
    public static final int MAX_FRAME_LEN = 8 * 1024 * 1024;
    /**
     * 客户端心跳
     */
    public static final String HEARD_PING = "Hi,Server";
    /**
     * 服务端心跳相应
     */
    public static final String HEARD_PONG = "Ye,Client";
}
