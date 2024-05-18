package cn.opentp.gossip.message;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 网络传输消息载体
 * 是所有消息的载体
 */
public class MessagePayload implements Serializable {

    // MessageTypeEnum
    private String type;
    private byte[] data;
    private String cluster;
    private String from;

    public MessagePayload() {
    }

    public MessagePayload(String type, byte[] data, String cluster, String from) {
        this.type = type;
        this.data = data;
        this.cluster = cluster;
        this.from = from;
    }

    public static MessagePayloadBuilder builder() {
        return new MessagePayloadBuilder();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "MessagePayload{" +
                "type='" + type + '\'' +
                ", data=" + Arrays.toString(data) +
                ", cluster='" + cluster + '\'' +
                ", from='" + from + '\'' +
                '}';
    }
}
