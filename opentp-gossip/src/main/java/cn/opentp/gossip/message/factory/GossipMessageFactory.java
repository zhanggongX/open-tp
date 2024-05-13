package cn.opentp.gossip.message.factory;

import cn.opentp.core.util.JSONUtils;
import cn.opentp.core.util.JacksonUtil;
import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.enums.MessageTypeEnum;
import cn.opentp.gossip.message.Ack2Message;
import cn.opentp.gossip.message.AckMessage;
import cn.opentp.gossip.message.GossipMessage;
import cn.opentp.gossip.message.MessagePayload;
import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.node.GossipNodeDigest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Gossip 消息工具类
 */
public class GossipMessageFactory {

    private static final Logger log = LoggerFactory.getLogger(GossipMessageFactory.class);
    private static final GossipMessageFactory factory = new GossipMessageFactory();

    public static GossipMessageFactory factory() {
        return factory;
    }

    private final GossipApp gossipApp = GossipApp.instance();

    private GossipMessageFactory() {
    }

    public ByteBuf encodeGossipMessage(GossipMessage gossipMessage) {
        String regularMessage = JSONUtils.toJSONString(gossipMessage);
        MessagePayload messagePayload = MessagePayload.builder().type(MessageTypeEnum.GOSSIP).data(regularMessage).cluster(gossipApp.setting().getCluster()).from(gossipApp.selfNode().socketAddress()).build();
        return Unpooled.copiedBuffer(JSONUtils.toJSONString(gossipMessage), StandardCharsets.UTF_8);
    }

    public ByteBuf encodeSyncMessage(List<GossipNodeDigest> digests) {
        String digestsJson = JSONUtils.toJSONString(digests);
        MessagePayload gossipMessage = MessagePayload.builder().type(MessageTypeEnum.SYNC).data(digestsJson).cluster(gossipApp.setting().getCluster()).from(gossipApp.selfNode().socketAddress()).build();
        return Unpooled.copiedBuffer(JSONUtils.toJSONString(gossipMessage), StandardCharsets.UTF_8);
    }

    public ByteBuf encodeAckMessage(AckMessage ackMessage) {
        String ackJson = JSONUtils.toJSONString(ackMessage);
        log.info("ackJso {}", ackJson);
        MessagePayload gossipMessage = MessagePayload.builder().type(MessageTypeEnum.ACK).data(ackJson).cluster(gossipApp.setting().getCluster()).from(gossipApp.selfNode().socketAddress()).build();
        return Unpooled.copiedBuffer(JSONUtils.toJSONString(gossipMessage), StandardCharsets.UTF_8);
    }

    public ByteBuf encodeAck2Message(Ack2Message ack2Message) {
        String ack2Json = JSONUtils.toJSONString(ack2Message);
        MessagePayload gossipMessage = MessagePayload.builder().type(MessageTypeEnum.ACK2).data(ack2Json).cluster(gossipApp.setting().getCluster()).from(gossipApp.selfNode().socketAddress()).build();
        return Unpooled.copiedBuffer(JSONUtils.toJSONString(gossipMessage), StandardCharsets.UTF_8);
    }

    public ByteBuf encodeShutdownMessage(GossipNode node) {
        String nodeJson = JSONUtils.toJSONString(node);
        MessagePayload gossipMessage = MessagePayload.builder().type(MessageTypeEnum.SHUTDOWN).data(nodeJson).cluster(gossipApp.setting().getCluster()).from(gossipApp.selfNode().socketAddress()).build();
        return Unpooled.copiedBuffer(JSONUtils.toJSONString(gossipMessage), StandardCharsets.UTF_8);
    }
}
