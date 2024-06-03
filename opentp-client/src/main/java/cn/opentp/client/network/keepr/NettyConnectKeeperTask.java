package cn.opentp.client.network.keepr;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.client.configuration.NettyReconnectProperties;
import cn.opentp.client.network.ThreadPoolReportService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 与服务端的连接保持
 * 为了方便定制不同重连策略所以没有使用 EventLoop 的 scheduleAtFixedRate
 */
public class NettyConnectKeeperTask implements Runnable {

    private final static long DEFAULT_INITIAL_DELAY = 5;
    private final static long DEFAULT_PERIOD = 5;

    public static void keep() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setDaemon(true);
            return thread;
        });

        NettyReconnectProperties nettyReconnectProperties = Configuration._cfg().nettyReconnectProperties();
        long initialDelay = nettyReconnectProperties.getInitialDelay() <= 0 ? DEFAULT_INITIAL_DELAY : nettyReconnectProperties.getInitialDelay();
        long period = nettyReconnectProperties.getInitialDelay() <= 0 ? DEFAULT_PERIOD : nettyReconnectProperties.getPeriod();

        scheduledExecutorService.scheduleAtFixedRate(new NettyConnectKeeperTask(), initialDelay, period, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        ThreadPoolReportService threadPoolReportService = Configuration._cfg().threadPoolReportService();
        threadPoolReportService.connectCheck();
    }
}
