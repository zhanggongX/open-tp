package cn.opentp.gossip.message;

import cn.opentp.gossip.node.GossipNode;

import java.io.Serializable;

/**
 * 流言传播消息
 */
public class GossipMessage implements Serializable {

    private static final long DEFAULT_TTL = 300000;

    private String messageId;
    // 有效时间
    private long effectTime;
    // 创建时间
    private long createTime;
    private Object payload;
    // 转发次数
    private int forwardCount;
    // 流言消息的创建者
    private GossipNode publishNode;

    public GossipMessage() {
    }

    public GossipMessage(GossipNode publishNode, Object payload) {
        this(publishNode, payload, DEFAULT_TTL);
    }

    public GossipMessage(GossipNode publishNode, Object payload, Long effectTime) {
        long now = System.currentTimeMillis();
        this.effectTime = effectTime == null ? DEFAULT_TTL : effectTime;
        this.publishNode = publishNode;
        this.payload = payload;
        this.messageId = "GSM_" + now;
        this.createTime = now;
        this.forwardCount = 0;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public long getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(long effectTime) {
        this.effectTime = effectTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public int getForwardCount() {
        return forwardCount;
    }

    public void setForwardCount(int forwardCount) {
        this.forwardCount = forwardCount;
    }

    public GossipNode getPublishNode() {
        return publishNode;
    }

    public void setPublishNode(GossipNode publishNode) {
        this.publishNode = publishNode;
    }

    @Override
    public String toString() {
        return "GossipMessage{" +
                "messageId='" + messageId + '\'' +
                ", effectTime=" + effectTime +
                ", createTime=" + createTime +
                ", payload=" + payload +
                ", forwardCount=" + forwardCount +
                ", publishNode=" + publishNode +
                '}';
    }
}
