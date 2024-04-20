package cn.opentp.client.example;

import cn.opentp.client.OpentpClientBootstrap;
import cn.opentp.client.configuration.Configuration;
import cn.opentp.core.thread.pool.ThreadPoolContext;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Example {

    public static void main(String[] args) {

        // 设置服务端地址
        List<InetSocketAddress> inetSocketAddresses = Configuration.configuration().serverAddresses();
        inetSocketAddresses.add(new InetSocketAddress("localhost", 9527));

        // 记录线程池信息
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 60, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1024));
        Configuration.configuration().threadPoolContextCache().put("threadPool", new ThreadPoolContext(threadPoolExecutor));

        // 开启服务
        new OpentpClientBootstrap().start();
    }
}