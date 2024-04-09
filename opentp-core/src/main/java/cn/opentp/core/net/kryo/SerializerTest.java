//package cn.opentp.core.tp.net.kryo;
//
//import cn.opentp.core.tp.ThreadPoolWrapper;
//
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//public class SerializerTest {
//
//    public void test(){
//        //测试对象
//        RPCResponse<User> rpcResponse = new RPCResponse<>();
//        rpcResponse.setData(new User("changlu", 123));
//
//        ThreadPoolWrapper threadPoolWrapper = new ThreadPoolWrapper(new ThreadPoolExecutor(1, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100)));
//        threadPoolWrapper.flush();
//
//        //测试kryo序列化
//        testSerialize(new KryoSerializer(), threadPoolWrapper);
//    }
//
//    public static void main(String[] args) {
//        SerializerTest serializerTest = new SerializerTest();
//        serializerTest.test();
//    }
//
//    public <T> void testSerialize(Serializer serializer, T t) {
//        System.out.println(String.format("=====开始序列化:%s=====", serializer.getClass()));
//        System.out.println("开始进行序列化");
//        long startTime = System.nanoTime();
//        //序列化
//        byte[] data = serializer.serialize(t);
//        long endTime = System.nanoTime();
//        System.out.println("  序列化时间为：" + (endTime - startTime) / 1000000000.0 + "秒");
//        System.out.println("  序列化后的内容为：" + new String(data));
//        System.out.println("  序列化后的长度为：" + data.length);
//        System.out.println("开始进行反序列化");
//        startTime = System.nanoTime();
//        //反序列化
//        Object deserialize = serializer.deserialize(data, t.getClass());
//        System.out.println("  反序列化后得到的对象为：" + deserialize);
//        endTime = System.nanoTime();
//        System.out.println("  反序列化时间为：" + (endTime - startTime) / 1000000000.0 + "秒");
//        System.out.println(String.format("=====结束序列化:%s=====", serializer.getClass()) + "\n");
//    }
//}