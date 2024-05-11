package cn.opentp.gossip.message.codec;

import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class Serializer {
    private static Serializer ourInstance = new Serializer();

    public static Serializer getInstance() {
        return ourInstance;
    }

    private Serializer() {
    }

    public ByteBuf encode(Serializable obj) {
        return Unpooled.copiedBuffer(JSON.toJSONString(obj), StandardCharsets.UTF_8);
    }

    public <T> T decode(ByteBuf byteBuf, Class<T> typeReference) {
        String str = byteBuf.toString(StandardCharsets.UTF_8);
        return JSON.parseObject(str, typeReference);
    }

}
