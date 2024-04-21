package cn.opentp.core.net;

import cn.opentp.core.thread.pool.ThreadPoolState;

import java.io.Serializable;
import java.util.List;

/**
 * opentp 自定义传输协议
 */
public class OpentpMessage implements Serializable, Cloneable {

    /**
     * 魔数
     */
    private byte[] magicNum;

    /**
     * 版本号
     */
    private byte[] version;

    /**
     * 消息体全长
     */
    private int length;

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

    public OpentpMessage(byte messageType, byte serializerType, long traceId, Object data) {
        this.messageType = messageType;
        this.serializerType = serializerType;
        this.traceId = traceId;
        this.data = data;
    }

    public OpentpMessage(byte[] magicNum, byte[] version) {
        this.magicNum = magicNum;
        this.version = version;
    }

    public byte[] getMagicNum() {
        return magicNum;
    }

    public void setMagicNum(byte[] magicNum) {
        this.magicNum = magicNum;
    }

    public byte[] getVersion() {
        return version;
    }

    public void setVersion(byte[] version) {
        this.version = version;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public byte getSerializerType() {
        return serializerType;
    }

    public void setSerializerType(byte serializerType) {
        this.serializerType = serializerType;
    }

    public long getTraceId() {
        return traceId;
    }

    public void setTraceId(long traceId) {
        this.traceId = traceId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public static OpentpMessageBuilder builder() {
        return new OpentpMessageBuilder();
    }

    @Override
    public OpentpMessage clone() {
        try {
            OpentpMessage clone = (OpentpMessage) super.clone();
            clone.setMagicNum(this.getMagicNum());
            clone.setVersion(this.getVersion());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}