package cn.opentp.core.net;

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
     * 消息内容
     */
    private byte[] content;

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

    public OpentpMessage build() {
        return new OpentpMessage(this.messageType, this.serializerType, this.traceId, this.data);
    }

    public void buildTo(OpentpMessage opentpMessage) {
        opentpMessage.setMessageType(this.messageType);
        opentpMessage.setSerializerType(this.serializerType);
        opentpMessage.setTraceId(this.traceId);
        opentpMessage.setData(this.data);
    }
}
