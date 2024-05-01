package cn.opentp.server.transport.codec;

import cn.opentp.core.net.BroadcastMessage;
import cn.opentp.core.net.BroadcastProtocol;
import cn.opentp.core.net.serializer.Serializer;
import cn.opentp.core.net.serializer.SerializerFactory;
import cn.opentp.core.net.serializer.SerializerTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BroadcastMessageEncoder extends MessageToByteEncoder<BroadcastProtocol> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BroadcastProtocol broadcastProtocol, ByteBuf byteBuf) throws Exception {
        try {
            encodeProtocol(broadcastProtocol);

            byteBuf.writeInt(broadcastProtocol.getLength());
            byteBuf.writeBytes(broadcastProtocol.getContent());
        } catch (Exception e) {
            log.error("BroadcastMessageEncoder encode error : ", e);
        }
    }

    private void encodeProtocol(BroadcastProtocol broadcastProtocol) {
        BroadcastMessage broadcastMessage = broadcastProtocol.getMessage();
        Serializer serializer = SerializerFactory.serializer(SerializerTypeEnum.Kryo.getType());

        byte[] bytes = serializer.serialize(broadcastMessage);
        broadcastProtocol.setLength(bytes.length);
        broadcastProtocol.setContent(bytes);
    }
}
