package cn.opentp.core.net.codec;

import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageConstant;
import cn.opentp.core.net.serializer.Serializer;
import cn.opentp.core.net.serializer.SerializerFactory;
import cn.opentp.core.util.MessageTraceIdUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpentpMessageEncoder extends MessageToByteEncoder<OpentpMessage> {

    private static final Logger log = LoggerFactory.getLogger(OpentpMessageEncoder.class);

    /**
     * 消息编码，Java bean -> byte[]
     *
     * @param channelHandlerContext channel ctx
     * @param opentpMessage         消息
     * @param byteBuf               输出对象
     * @throws Exception ex
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, OpentpMessage opentpMessage, ByteBuf byteBuf) throws Exception {
        try {
            encodeMessage(opentpMessage);

            byteBuf.writeBytes(opentpMessage.getMagicNum());
            byteBuf.writeBytes(opentpMessage.getVersion());
            byteBuf.writeInt(opentpMessage.getLength());
            byteBuf.writeByte(opentpMessage.getMessageType());
            byteBuf.writeByte(opentpMessage.getSerializerType());
            byteBuf.writeLong(MessageTraceIdUtil.traceId());
            byteBuf.writeInt(opentpMessage.getLicenseBytes().length);
            byteBuf.writeBytes(opentpMessage.getLicenseBytes());
            byteBuf.writeBytes(opentpMessage.getContent());
        } catch (Exception e) {
            log.error("OpentpMessageDecoder encode error : ", e);
        }
    }

    /**
     * 消息对象编码
     *
     * @param opentpMessage 消息协议
     */
    private void encodeMessage(OpentpMessage opentpMessage) {
        Serializer serializer = SerializerFactory.serializer(opentpMessage.getSerializerType());
        opentpMessage.setLicenseBytes(serializer.serialize(opentpMessage.getLicenseKey()));
        opentpMessage.setContent(serializer.serialize(opentpMessage.getData()));
        opentpMessage.setLength(OpentpMessageConstant.MESSAGE_HEAD_LENGTH + opentpMessage.getLicenseBytes().length + opentpMessage.getContent().length);
    }
}
