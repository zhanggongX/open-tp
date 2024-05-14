package cn.opentp.gossip.message.factory;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.enums.MessageTypeEnum;
import cn.opentp.gossip.message.*;
import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.util.GossipJacksonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * Gossip 消息工具类
 */
public class GossipMessageFactory {

    private static final GossipMessageFactory factory = new GossipMessageFactory();

    public static GossipMessageFactory factory() {
        return factory;
    }

    private final GossipApp gossipApp = GossipApp.instance();

    private GossipMessageFactory() {
    }

    public ByteBuf encodeGossipMessage(GossipMessage gossipMessage) {
        String gossipMessageJson = GossipJacksonUtil.toJSONString(gossipMessage);
        MessagePayload messagePayload = MessagePayload.builder().type(MessageTypeEnum.GOSSIP).data(gossipMessageJson).cluster(gossipApp.setting().getCluster()).from(gossipApp.selfNode().socketAddress()).build();
        return Unpooled.copiedBuffer(GossipJacksonUtil.toJSONString(messagePayload), StandardCharsets.UTF_8);
    }

    public ByteBuf encodeSyncMessage(SyncMessage syncMessage) {
        String syncMessageJson = GossipJacksonUtil.toJSONString(syncMessage);
        MessagePayload messagePayload = MessagePayload.builder().type(MessageTypeEnum.SYNC).data(syncMessageJson).cluster(gossipApp.setting().getCluster()).from(gossipApp.selfNode().socketAddress()).build();
        return Unpooled.copiedBuffer(GossipJacksonUtil.toJSONString(messagePayload), StandardCharsets.UTF_8);
    }

    public ByteBuf encodeAckMessage(AckMessage ackMessage) {
        String ackJson = GossipJacksonUtil.toJSONString(ackMessage);
        MessagePayload messagePayload = MessagePayload.builder().type(MessageTypeEnum.ACK).data(ackJson).cluster(gossipApp.setting().getCluster()).from(gossipApp.selfNode().socketAddress()).build();
        return Unpooled.copiedBuffer(GossipJacksonUtil.toJSONString(messagePayload), StandardCharsets.UTF_8);
    }

    public ByteBuf encodeAck2Message(Ack2Message ack2Message) {
        String ack2Json = GossipJacksonUtil.toJSONString(ack2Message);
        MessagePayload messagePayload = MessagePayload.builder().type(MessageTypeEnum.ACK2).data(ack2Json).cluster(gossipApp.setting().getCluster()).from(gossipApp.selfNode().socketAddress()).build();
        return Unpooled.copiedBuffer(GossipJacksonUtil.toJSONString(messagePayload), StandardCharsets.UTF_8);
    }

    public ByteBuf encodeShutdownMessage(GossipNode node) {
        String nodeJson = GossipJacksonUtil.toJSONString(node);
        MessagePayload messagePayload = MessagePayload.builder().type(MessageTypeEnum.SHUTDOWN).data(nodeJson).cluster(gossipApp.setting().getCluster()).from(gossipApp.selfNode().socketAddress()).build();
        return Unpooled.copiedBuffer(GossipJacksonUtil.toJSONString(messagePayload), StandardCharsets.UTF_8);
    }
}
