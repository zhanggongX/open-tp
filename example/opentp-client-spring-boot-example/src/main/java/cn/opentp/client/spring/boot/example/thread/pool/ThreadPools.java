package cn.opentp.client.spring.boot.example.thread.pool;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.core.thread.pool.ThreadPoolWrapper;
import opentp.client.spring.boot.starter.annotation.Opentp;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ThreadPools {

    /**
     * 注解方式获得线程池信息
     */
    @Opentp("threadPoolExecutor1")
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(5, 10, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
    }

    public static ThreadPoolExecutor threadPoolExecutor2;
    private static final int INIT_THREAD_NUM = 4;
    private static final int MAX_THREAD_NUM = 16;
    private static final int WORK_QUEUE_CAPACITY = 100;
    private static final int KEEP_ALIVE_TIME_MINUTES = 60;

    static {
        ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(WORK_QUEUE_CAPACITY);
        threadPoolExecutor2 = new ThreadPoolExecutor(INIT_THREAD_NUM, MAX_THREAD_NUM, KEEP_ALIVE_TIME_MINUTES, TimeUnit.MINUTES, workQueue);

        // 手动创建线程池方式
        Configuration._cfg().threadPoolContextCache().put("threadPoolExecutor2", new ThreadPoolWrapper(threadPoolExecutor2));
    }

    public static void execute(Runnable command) throws Exception {
        try {
            threadPoolExecutor2.execute(command);
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e.getCause());
        }
    }
}
