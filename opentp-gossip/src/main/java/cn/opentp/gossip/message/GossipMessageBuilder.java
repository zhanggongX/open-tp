package cn.opentp.gossip.message;

import cn.opentp.gossip.enums.MessageTypeEnum;

public class GossipMessageBuilder {

    // MessageTypeEnum
    private String type;
    private Object data;
    private String cluster;
    private String from;

    public GossipMessageBuilder type(MessageTypeEnum messageTypeEnum) {
        this.type = messageTypeEnum.getType();
        return this;
    }

    public GossipMessageBuilder data(Object data) {
        this.data = data;
        return this;
    }

    public GossipMessageBuilder cluster(String cluster) {
        this.cluster = cluster;
        return this;
    }

    public GossipMessageBuilder from(String from) {
        this.from = from;
        return this;
    }

    public GossipMessage build() {
        return new GossipMessage(this.type, this.data, this.cluster, this.from);
    }
}
