package cn.opentp.core.net.handler;

import cn.opentp.core.net.serializer.kryo.KryoSerializer;
import cn.opentp.core.thread.pool.ThreadPoolState;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 自定义解码器（将字节数组变为对象）
 */
public class ThreadPoolStateDecoder extends ByteToMessageDecoder {
    // 表示数据流（头部是消息长度）头部的字节数
    private static final int HEAD_LENGTH = 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < HEAD_LENGTH) {
            return;
        }
        // 标记当前readIndex的位置
        in.markReaderIndex();
        // 读取传送过来的消息长度，ByteBuf的 readInt() 方法会让它的readIndex+4
        int dataLength = in.readInt();
        if (dataLength <= 0) {// 如果读到的消息长度不大于0，这是不应该出现的情况，关闭连接
            ctx.close();
        }
        if (in.readableBytes() < dataLength) { // 说明是不完整的报文，重置readIndex
            in.resetReaderIndex();
            return;
        }

        // 至此，读取到一条完整报文
        byte[] body = new byte[dataLength];
        in.readBytes(body);

        // 将bytes数组转换为我们需要的对象
        KryoSerializer kryoSerializer = new KryoSerializer();
        ThreadPoolState msg = kryoSerializer.deserialize(body, ThreadPoolState.class);
        out.add(msg);
    }
}