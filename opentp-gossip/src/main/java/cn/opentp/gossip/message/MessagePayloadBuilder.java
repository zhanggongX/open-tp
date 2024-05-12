package cn.opentp.gossip.message;

import cn.opentp.gossip.enums.MessageTypeEnum;

public class MessagePayloadBuilder {

    // MessageTypeEnum
    private String type;
    private String data;
    private String cluster;
    private String from;

    public MessagePayloadBuilder type(MessageTypeEnum messageTypeEnum) {
        this.type = messageTypeEnum.name();
        return this;
    }

    public MessagePayloadBuilder data(String data) {
        this.data = data;
        return this;
    }

    public MessagePayloadBuilder cluster(String cluster) {
        this.cluster = cluster;
        return this;
    }

    public MessagePayloadBuilder from(String from) {
        this.from = from;
        return this;
    }

    public MessagePayload build() {
        return new MessagePayload(this.type, this.data, this.cluster, this.from);
    }
}
