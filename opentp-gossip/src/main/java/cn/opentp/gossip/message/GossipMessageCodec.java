package cn.opentp.gossip.message;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.core.GossipRegularMessage;
import cn.opentp.gossip.enums.MessageTypeEnum;
import cn.opentp.gossip.model.GossipDigest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class GossipMessageCodec {

    private static final GossipMessageCodec codec = new GossipMessageCodec();

    public static GossipMessageCodec codec() {
        return codec;
    }

    private final GossipApp gossipApp = GossipApp.instance();

    public ByteBuf encodeRegularMessage(GossipRegularMessage gossipRegularMessage) {
//        String regularMessage = JSON.toJSONString(gossipRegularMessage);
        GossipMessage gossipMessage = GossipMessage.builder()
                .type(MessageTypeEnum.REG_MESSAGE)
                .data(gossipRegularMessage)
                .cluster(gossipApp.setting().getCluster())
                .from(gossipApp.selfNode().socketAddress()).build();
        return Unpooled.copiedBuffer(JSON.toJSONString(gossipMessage), StandardCharsets.UTF_8);
    }

    public ByteBuf encodeSyncMessage(List<GossipDigest> digests) {
        GossipMessage gossipMessage = GossipMessage.builder()
                .type(MessageTypeEnum.SYNC_MESSAGE)
                .data(digests)
                .cluster(gossipApp.setting().getCluster())
                .from(gossipApp.selfNode().socketAddress()).build();
        return Unpooled.copiedBuffer(JSON.toJSONString(gossipMessage), StandardCharsets.UTF_8);
    }

    public ByteBuf encodeAckMessage(AckMessage ackMessage) {
        String ackJson = JSON.toJSONString(ackMessage);
        GossipMessage gossipMessage = GossipMessage.builder()
                .type(MessageTypeEnum.ACK_MESSAGE)
                .data(ackMessage)
                .cluster(gossipApp.setting().getCluster())
                .from(gossipApp.selfNode().socketAddress()).build();
        return Unpooled.copiedBuffer(JSON.toJSONString(gossipMessage), StandardCharsets.UTF_8);
    }

    public ByteBuf encodeAck2Message(Ack2Message ack2Message) {
        String ack2Json = JSON.toJSONString(ack2Message);
        GossipMessage gossipMessage = GossipMessage.builder()
                .type(MessageTypeEnum.ACK2_MESSAGE)
                .data(ack2Message)
                .cluster(gossipApp.setting().getCluster())
                .from(gossipApp.selfNode().socketAddress()).build();
        return Unpooled.copiedBuffer(JSON.toJSONString(gossipMessage), StandardCharsets.UTF_8);
    }

    public ByteBuf encodeShutdownMessage() {
        GossipMessage gossipMessage = GossipMessage.builder()
                .type(MessageTypeEnum.SHUTDOWN)
                .data(gossipApp.selfNode())
                .cluster(gossipApp.setting().getCluster())
                .from(gossipApp.selfNode().socketAddress()).build();
        return Unpooled.copiedBuffer(JSON.toJSONString(gossipMessage), StandardCharsets.UTF_8);
    }
}
