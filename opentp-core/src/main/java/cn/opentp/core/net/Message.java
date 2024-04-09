package cn.opentp.core.net;

import java.io.Serializable;

public class Message implements Serializable {

    /**
     * 消息类型
     */
    private byte type;

    /**
     * 消息长度
     */
    private int length;

    /**
     * 消息内容
     */
    private byte[] content;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}