package cn.opentp.core.net.handler;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.auth.License;
import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageConstant;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.core.net.serializer.Serializer;
import cn.opentp.core.net.serializer.SerializerFactory;
import cn.opentp.core.thread.pool.ThreadPoolState;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class OpentpMessageDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger log = LoggerFactory.getLogger(OpentpMessageDecoder.class);

    /**
     * 配置消息的消息头信息
     */
    public OpentpMessageDecoder() {
        /*
         * lengthFieldOffset   = 6         3 byte magicNum + 3 byte version
         * lengthFieldLength   = 4         length is int.
         * lengthAdjustment    = -10       - 3 byte magicNum - 3 byte version - 4 byte length field
         * initialBytesToStrip = 0         strip 0 byte.
         */
        super(OpentpMessageConstant.MAX_FRAME_LEN, 6, 4, -10, 0);
    }

    /**
     * 执行解码
     *
     * @param ctx     当前 handler 的 context
     * @param byteBuf 输入的数据
     * @return 解码数据
     * @throws Exception ex
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        Object decodeObj = super.decode(ctx, byteBuf);

        if (decodeObj instanceof ByteBuf decodeBuf) {
            try {
                return decodeMessage(decodeBuf);
            } catch (Exception e) {
                log.error("OpentpMessageDecoder decode error : ", e);
            } finally {
                // 池化消息释放
                decodeBuf.release();
            }
        }

        return decodeObj;
    }

    /**
     * decode message
     *
     * @param byteBuf 数据
     * @return OpentpMessage
     */
    private Object decodeMessage(ByteBuf byteBuf) {

        checkMagic(byteBuf);
        checkVersion(byteBuf);

        int length = byteBuf.readInt();
        byte messageType = byteBuf.readByte();
        byte serializerType = byteBuf.readByte();
        long traceId = byteBuf.readLong();
        int licenseKeyLength = byteBuf.readInt();
        byte[] licenseKeyBytes = new byte[licenseKeyLength];
        byteBuf.readBytes(licenseKeyBytes);

        Serializer serializer = SerializerFactory.serializer(serializerType);
        String licenseKey = serializer.deserialize(licenseKeyBytes, String.class);

        OpentpMessage opentpMessage = OpentpMessage
                .builder()
                .messageType(messageType)
                .serializerType(serializerType)
                .traceId(traceId)
                .licenseKey(licenseKey)
                .build();
        /*
         * 实际数据长度，减去头部信息长度，减去 licenseKey 长度，减去 licenseKeyLength 长度
         */
        length -= (OpentpMessageConstant.MESSAGE_HEAD_LENGTH + licenseKeyLength);

        if (length <= 0) return opentpMessage;

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        OpentpMessageTypeEnum messageTypeEnum = OpentpMessageTypeEnum.parse(messageType);
        switch (Objects.requireNonNull(messageTypeEnum)) {
            case HEART_PING:
                String ping = serializer.deserialize(bytes, String.class);
                opentpMessage.setData(ping);
                break;
            case THREAD_POOL_EXPORT:
                List<?> threadPoolStates = serializer.deserialize(bytes, ArrayList.class);
                opentpMessage.setData(threadPoolStates);
                break;
            case THREAD_POOL_UPDATE:
                ThreadPoolState threadPoolState = serializer.deserialize(bytes, ThreadPoolState.class);
                opentpMessage.setData(threadPoolState);
                break;
            case AUTHENTICATION_REQ:
                ClientInfo clientInfo = serializer.deserialize(bytes, ClientInfo.class);
                opentpMessage.setData(clientInfo);
                break;
            case AUTHENTICATION_RES:
                License opentpLicense = serializer.deserialize(bytes, License.class);
                opentpMessage.setData(opentpLicense);
                break;
        }

        return opentpMessage;
    }

    /**
     * 校验魔数
     *
     * @param byteBuf 消息
     * @author zg
     */
    private void checkVersion(ByteBuf byteBuf) {
        byte[] messageMagic = new byte[OpentpMessageConstant.VERSION.length];
        // readIndex 会自动增加。
        byteBuf.readBytes(messageMagic);
        if (!Arrays.equals(messageMagic, OpentpMessageConstant.VERSION)) {
            throw new IllegalArgumentException("error msg : version unequal");
        }
    }

    /**
     * 校验版本
     *
     * @param byteBuf 消息
     * @author zg
     */
    private void checkMagic(ByteBuf byteBuf) {
        byte[] version = new byte[OpentpMessageConstant.MAGIC.length];
        // readIndex 会自动增加。
        byteBuf.readBytes(version);
        if (!Arrays.equals(version, OpentpMessageConstant.MAGIC)) {
            throw new IllegalArgumentException("error msg : magic num unequal");
        }
    }
}
