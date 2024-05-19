package cn.opentp.gossip.message.codec;

import cn.opentp.core.net.serializer.Serializer;
import cn.opentp.core.net.serializer.SerializerFactory;
import cn.opentp.core.net.serializer.SerializerTypeEnum;
import cn.opentp.gossip.GossipEnvironment;
import cn.opentp.gossip.GossipSettings;
import cn.opentp.gossip.enums.MessageTypeEnum;
import cn.opentp.gossip.message.*;
import cn.opentp.gossip.node.GossipNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Gossip 消息工具类
 */
public class GossipMessageCodec {

    private static final GossipMessageCodec codec = new GossipMessageCodec();

    public static GossipMessageCodec codec() {
        return codec;
    }

    private final GossipEnvironment environment = GossipEnvironment.instance();
    private final GossipSettings settings = environment.setting();

    private GossipMessageCodec() {
    }

    public ByteBuf encodeGossipMessage(GossipMessage gossipMessage) {
        Serializer serializer = SerializerFactory.serializer(SerializerTypeEnum.Kryo.getType());
        byte[] gossipMessageByte = serializer.serialize(gossipMessage);
        MessagePayload messagePayload = MessagePayload.builder().type(MessageTypeEnum.GOSSIP).data(gossipMessageByte).cluster(settings.getCluster()).from(environment.selfNode().socketAddress()).build();
        return buildByteBuf(serializer, messagePayload);
    }

    public ByteBuf encodeSyncMessage(SyncMessage syncMessage) {
        Serializer serializer = SerializerFactory.serializer(SerializerTypeEnum.Kryo.getType());
        byte[] syncMessageByte = serializer.serialize(syncMessage);
        MessagePayload messagePayload = MessagePayload.builder().type(MessageTypeEnum.SYNC).data(syncMessageByte).cluster(settings.getCluster()).from(environment.selfNode().socketAddress()).build();
        return buildByteBuf(serializer, messagePayload);
    }

    public ByteBuf encodeAckMessage(AckMessage ackMessage) {
        Serializer serializer = SerializerFactory.serializer(SerializerTypeEnum.Kryo.getType());
        byte[] ackMessageByte = serializer.serialize(ackMessage);
        MessagePayload messagePayload = MessagePayload.builder().type(MessageTypeEnum.ACK).data(ackMessageByte).cluster(settings.getCluster()).from(environment.selfNode().socketAddress()).build();
        return buildByteBuf(serializer, messagePayload);
    }

    public ByteBuf encodeAck2Message(Ack2Message ack2Message) {
        Serializer serializer = SerializerFactory.serializer(SerializerTypeEnum.Kryo.getType());
        byte[] ack2MessageByte = serializer.serialize(ack2Message);
        MessagePayload messagePayload = MessagePayload.builder().type(MessageTypeEnum.ACK2).data(ack2MessageByte).cluster(settings.getCluster()).from(environment.selfNode().socketAddress()).build();
        return buildByteBuf(serializer, messagePayload);
    }

    public ByteBuf encodeShutdownMessage(GossipNode node) {
        Serializer serializer = SerializerFactory.serializer(SerializerTypeEnum.Kryo.getType());
        byte[] nodeByte = serializer.serialize(node);
        MessagePayload messagePayload = MessagePayload.builder().type(MessageTypeEnum.SHUTDOWN).data(nodeByte).cluster(settings.getCluster()).from(environment.selfNode().socketAddress()).build();
        return buildByteBuf(serializer, messagePayload);
    }

    private ByteBuf buildByteBuf(Serializer serializer, MessagePayload messagePayload) {
        byte[] messagePayloadByte = serializer.serialize(messagePayload);
        return Unpooled.copiedBuffer(messagePayloadByte);
    }

    public MessagePayload decodeMessage(ByteBuf byteBuf) {
        Serializer serializer = SerializerFactory.serializer(SerializerTypeEnum.Kryo.getType());
        int count = byteBuf.readableBytes();
        byte[] content = new byte[count];
        byteBuf.readBytes(content);
        return serializer.deserialize(content, MessagePayload.class);
    }

    public <T> T decodeMessage(byte[] content, Class<T> tClass) {
        Serializer serializer = SerializerFactory.serializer(SerializerTypeEnum.Kryo.getType());
        return serializer.deserialize(content, tClass);
    }
}
