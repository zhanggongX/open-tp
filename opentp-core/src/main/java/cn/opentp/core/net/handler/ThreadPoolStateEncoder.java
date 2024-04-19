package cn.opentp.core.net.handler;

import cn.opentp.core.net.kryo.KryoSerializer;
import cn.opentp.core.thread.pool.ThreadPoolState;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义kryo编码器(将传输对象变为byte数组)
 *
 * @author stone
 * @date 2019/7/30 14:16
 */
public class ThreadPoolStateEncoder extends MessageToByteEncoder<ThreadPoolState> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ThreadPoolState threadPoolState, ByteBuf out) throws Exception {
        // 1. 将对象转换为byte
        KryoSerializer kryoSerializer = new KryoSerializer();
        byte[] body = kryoSerializer.serialize(threadPoolState);
        // 2. 读取消息的长度
        int dataLength = body.length;
        // 3. 先将消息长度写入，也就是消息头
        out.writeInt(dataLength);
        out.writeBytes(body);
    }
}
