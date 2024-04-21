package cn.opentp.core.net;

import org.apache.logging.log4j.util.Strings;

public class OpentpMessageBuilder {

    /**
     * 消息类型
     */
    private byte messageType;

    /**
     * 串行化类型
     */
    private byte serializerType;

    /**
     * 消息链路 ID
     */
    private long traceId;

    /**
     * 传输的数据
     */
    private Object data;

    /**
     * 认证码
     */
    private String licenseKey;

    public OpentpMessageBuilder messageType(byte messageType) {
        this.messageType = messageType;
        return this;
    }

    public OpentpMessageBuilder serializerType(byte serializerType) {
        this.serializerType = serializerType;
        return this;
    }

    public OpentpMessageBuilder traceId(long traceId) {
        this.traceId = traceId;
        return this;
    }

    public OpentpMessageBuilder data(Object data) {
        this.data = data;
        return this;
    }

    public OpentpMessageBuilder licenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
        return this;
    }

    public OpentpMessage build() {
        if (this.licenseKey == null) {
            this.licenseKey = Strings.EMPTY;
        }
        return new OpentpMessage(this.messageType, this.serializerType, this.traceId, this.data, this.licenseKey);
    }

    public void buildTo(OpentpMessage opentpMessage) {
        if (this.licenseKey == null) {
            this.licenseKey = Strings.EMPTY;
        }
        opentpMessage.setMessageType(this.messageType);
        opentpMessage.setSerializerType(this.serializerType);
        opentpMessage.setTraceId(this.traceId);
        opentpMessage.setData(this.data);
        opentpMessage.setLicenseKey(this.licenseKey);
    }
}
