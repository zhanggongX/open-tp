package cn.opentp.server.transport.codec;

import cn.opentp.core.net.BroadcastMessage;
import cn.opentp.core.net.serializer.Serializer;
import cn.opentp.core.net.serializer.SerializerFactory;
import cn.opentp.core.net.serializer.SerializerTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class BroadcastMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        int HEAD_LENGTH = 4;
        if (byteBuf.readableBytes() < HEAD_LENGTH) {
            return;
        }

        // 标记当前readIndex的位置
        byteBuf.markReaderIndex();

        // 读取传送过来的消息长度，ByteBuf的 readInt() 方法会让它的readIndex+4
        int dataLength = byteBuf.readInt();
        // 如果读到的消息长度不大于0，这是不应该出现的情况，关闭连接
        if (dataLength <= 0) {
            ctx.close();
        }
        // 说明是不完整的报文，重置readIndex
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        // 至此，读取到一条完整报文
        byte[] content = new byte[dataLength];
        byteBuf.readBytes(content);

        // 将bytes数组转换为我们需要的对象
        Serializer serializer = SerializerFactory.serializer(SerializerTypeEnum.Kryo.getType());
        BroadcastMessage broadcastMessage = serializer.deserialize(content, BroadcastMessage.class);

        list.add(broadcastMessage);
    }
}
