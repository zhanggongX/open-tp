package cn.opentp.client.spring.boot.example.tp;

import opentp.client.spring.boot.starter.annotation.Opentp;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class TpDemo {

    @Opentp("demoExecutor")
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(10, 200, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024));
    }

    @Opentp("demoExecutor1")
    @Bean
    public ThreadPoolExecutor threadPoolExecutor1() {
        return new ThreadPoolExecutor(10, 200, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1024));
    }
}
