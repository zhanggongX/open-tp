package cn.opentp.client.network.worker;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.client.configuration.ThreadPoolStateReportProperties;
import cn.opentp.client.network.ThreadPoolReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 线程状态上报线程
 * 使用自定义的调度线程
 * 使用 eventLoop 的 scheduleAtFixedRate 发生重连的时候
 * 产生多个 channel 可能在一个 eventLoop 也可能在多个 eventLoop
 * 上报线程不可控制
 */
public class ThreadPoolStateExportTask implements Runnable {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static long DEFAULT_INITIAL_DELAY = 5;
    private final static long DEFAULT_PERIOD = 1;

    public static void report() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        ThreadPoolStateReportProperties threadPoolStateReportProperties = Configuration._cfg().threadPoolStateReportProperties();
        long initialDelay = threadPoolStateReportProperties.getInitialDelay() <= 0 ? DEFAULT_INITIAL_DELAY : threadPoolStateReportProperties.getInitialDelay();
        long period = threadPoolStateReportProperties.getInitialDelay() <= 0 ? DEFAULT_PERIOD : threadPoolStateReportProperties.getPeriod();

        scheduledExecutorService.scheduleAtFixedRate(new ThreadPoolStateExportTask(), initialDelay, period, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        ThreadPoolReportService threadPoolReportService = Configuration._cfg().threadPoolReportService();
        threadPoolReportService.sendReport();
    }
}
