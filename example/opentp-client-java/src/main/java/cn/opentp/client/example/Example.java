package cn.opentp.client.example;

import cn.opentp.client.OpentpClientBootstrap;
import cn.opentp.client.configuration.Configuration;
import cn.opentp.core.thread.pool.ThreadPoolWrapper;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Example {

    public static void main(String[] args) {

        // 设置服务端地址
        List<InetSocketAddress> inetSocketAddresses = Configuration._cfg().serverAddresses();
        inetSocketAddresses.add(new InetSocketAddress("10.253.52.34", 9527));

        // 设置断网重连重试周期，非必填
        Configuration._cfg().reconnectProps().setInitialDelay(5);
        Configuration._cfg().reconnectProps().setPeriod(5);

        // 线程上报信息周期，非必填
        Configuration._cfg().reportProps().setInitialDelay(2);
        Configuration._cfg().reportProps().setPeriod(2);

        Configuration._cfg().clientInfo().setAppKey("5eadfdc9bb22437f8cd3b49d9777d35a");
        Configuration._cfg().clientInfo().setAppSecret("b7d54462-c055-4669-bcd4-956d0c844a2b");

        // 记录线程池信息
        ThreadPoolExecutor tp1 = new ThreadPoolExecutor(10, 20, 60, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1024));
        Configuration._cfg().threadPoolContextCache().put("tp1", new ThreadPoolWrapper(tp1));

        ThreadPoolExecutor tp2 = new ThreadPoolExecutor(10, 20, 60, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1024));
        Configuration._cfg().threadPoolContextCache().put("tp2", new ThreadPoolWrapper(tp2));

        // 开启服务
        new OpentpClientBootstrap().startup();
    }
}
