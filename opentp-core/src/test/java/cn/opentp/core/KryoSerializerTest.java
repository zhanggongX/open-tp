//package cn.opentp.core;
//
//import cn.opentp.core.tp.ThreadPoolWrapper;
//import cn.opentp.core.tp.kryo.KryoSerialization2;
//import cn.opentp.core.tp.kryo.ObjectOutput;
//import cn.opentp.core.tp.kryo.Serialization;
//import cn.opentp.core.tp.kryo.ThreadLocalKryoFactory;
//import cn.opentp.core.tp.net.serializer.Serializer;
//import cn.opentp.core.tp.net.serializer.SerializerFactory;
//import org.junit.jupiter.api.Test;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
////import cn.opentp.core.tp.ThreadPoolWrapper;
//
//
//class KryoSerializerTest {
//
//    protected Serialization serialization = new KryoSerialization2();
//    protected ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//    @Test
//    void serialize() throws IOException {
//        ThreadPoolWrapper threadPoolWrapper = new ThreadPoolWrapper(new ThreadPoolExecutor(1, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100)));
//
//        ObjectOutput objectOutput = serialization.serialize(null, byteArrayOutputStream);
//        objectOutput.writeObject(threadPoolWrapper);
//        objectOutput.flushBuffer();
//        System.out.println(1);
//    }
//
//    @Test
//    void deserialize() {
//    }
//}