package cn.opentp.core.tp;

import cn.opentp.core.tp.net.serializer.Serializer;
import cn.opentp.core.tp.net.serializer.SerializerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
//import cn.opentp.core.tp.ThreadPoolWrapper;


class KryoSerializerTest {

    private static Serializer serializer;

    @BeforeAll
    static void serializer() {
        serializer = SerializerFactory.getSerializer(ThreadPoolWrapper.class);
    }

    @Test
    void serialize() {
//        ThreadPoolWrapper threadPoolWrapper = new ThreadPoolWrapper(new ThreadPoolExecutor(1, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100)));
//        byte[] bytes = serializer.serialize(threadPoolWrapper);
//        System.out.println(1);

    }

    @Test
    void deserialize() {
    }
}