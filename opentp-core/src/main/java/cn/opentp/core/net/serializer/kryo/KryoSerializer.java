package cn.opentp.core.net.serializer.kryo;

import cn.opentp.core.net.serializer.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer implements Serializer {

    //由于Kryo是线程不安全的，所以我们这里使用ThreadLocal来解决线程安全问题
    public static ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {

        Kryo kryo = new Kryo();
        //检测循环依赖，默认值为true，避免版本变化显示设置
        kryo.setReferences(true);
        //方式一：设置无需注册，那么之后就无需对需要进行序列号的类进行注册（性能略差）
        //默认值为true，避免版本变化显示设置
        kryo.setRegistrationRequired(false);
        //设置默认的实例化器
//        ((DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
        //方式二：由于默认是需要进行注册的，若是不设置为false，那么就需要进行手动注册class类
//        kryo.register(ThreadPoolState.class);
//        kryo.register(String.class);
//        kryo.register(ArrayList.class);
//        kryo.register(ClientInfo.class);
//        kryo.register(License.class);
//        kryo.register(BroadcastProtocol.class);
//        kryo.register(BroadcastMessage.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             final Output output = new Output(baos)
        ) {
            Kryo kryo = kryoThreadLocal.get();
            //进行序列化
            kryo.writeObject(output, obj);
//            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (IOException e) {
            throw new RuntimeException("Serialization failed");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object obj = kryo.readObject(input, clazz);
//            kryoThreadLocal.remove();
            return clazz.cast(obj);
        } catch (IOException e) {
            throw new RuntimeException("Serialization failed");
        }
    }

//    public static void main(String[] args) {
//
//        byte[] bytes = new byte[]{1, 1, 111, 112, 101, 110, 116, 112, 45, 99, 108, 105, 101, 110, 116, 45, 106, 97, 118, -31, 1, 49, 50, 51, 52, 53, -74, 1, 49, 57, 50, 46, 49, 54, 56, 46, 51, 49, 46, 49, 56, -79, 1, 97, 98, 54, 52};
//        KryoSerializer kryoSerializer = new KryoSerializer();
//        OpentpAuthentication opentpAuthentication = new OpentpAuthentication();
//        opentpAuthentication.setHost("192");
//        opentpAuthentication.setAppKey("java");
//        opentpAuthentication.setAppSecret("123");
//        opentpAuthentication.setInstance("aaaa");
//        byte[] a = kryoSerializer.serialize(opentpAuthentication);
//
//        AuthClone b = kryoSerializer.deserialize(bytes, AuthClone.class);
//
//        byte[] c = kryoSerializer.serialize("");
//
//        String d = kryoSerializer.deserialize(c, String.class);
//
//        System.out.println(1);
//    }
}