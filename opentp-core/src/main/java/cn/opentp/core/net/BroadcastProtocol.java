package cn.opentp.core.net;

/**
 * 广播消息协议
 */
public class BroadcastProtocol {

    private int length;
    private BroadcastMessage message;
    private byte[] content;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public BroadcastMessage getMessage() {
        return message;
    }

    public void setMessage(BroadcastMessage message) {
        this.message = message;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
