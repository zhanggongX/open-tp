package cn.opentp.client.net.worker;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.client.configuration.ThreadPoolStateReportProperties;
import cn.opentp.core.constant.OpentpCoreConstant;
import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.core.thread.pool.ThreadPoolContext;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.core.util.MessageTraceIdUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private final static long DEFAULT_INITIAL_DELAY = 1;
    private final static long DEFAULT_PERIOD = 1;

    public static void startup() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        ThreadPoolStateReportProperties threadPoolStateReportProperties = Configuration.configuration().threadPoolStateReportProperties();
        long initialDelay = threadPoolStateReportProperties.getInitialDelay() <= 0 ? DEFAULT_INITIAL_DELAY : threadPoolStateReportProperties.getInitialDelay();
        long period = threadPoolStateReportProperties.getInitialDelay() <= 0 ? DEFAULT_PERIOD : threadPoolStateReportProperties.getPeriod();

        scheduledExecutorService.scheduleAtFixedRate(new ThreadPoolStateExportTask(), initialDelay, period, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        Channel channel = Configuration.configuration().threadPoolStateReportChannel();
        // 连接为空，连接断开，连接 licenseKey 为空，都不发送消息
        if (channel == null || !channel.isActive() || channel.attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).get().isEmpty()) {
            return;
        }

        List<ThreadPoolState> threadPoolStates = new ArrayList<>();

        Map<String, ThreadPoolContext> threadPoolContextCache = Configuration.configuration().threadPoolContextCache();
        for (Map.Entry<String, ThreadPoolContext> threadPoolContextEntry : threadPoolContextCache.entrySet()) {
            String threadPoolKey = threadPoolContextEntry.getKey();
            ThreadPoolContext threadPoolContext = threadPoolContextEntry.getValue();
            threadPoolContext.flushStateAndSetThreadPoolName(threadPoolKey);
            threadPoolStates.add(threadPoolContext.getState());
        }

        // 克隆信息，自带魔数和版本号
        OpentpMessage opentpMessage = OpentpCoreConstant.OPENTP_MSG_PROTO.clone();
        OpentpMessage
                .builder()
                .messageType(OpentpMessageTypeEnum.THREAD_POOL_EXPORT.getCode())
                .serializerType(OpentpMessageTypeEnum.THREAD_POOL_EXPORT.getCode())
                .traceId(MessageTraceIdUtil.traceId())
                .licenseKey(channel.attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).get())
                .data(threadPoolStates)
                .buildTo(opentpMessage);

        ChannelFuture channelFuture = channel.writeAndFlush(opentpMessage);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    log.debug("上报线程消息成功");
                } else {
                    log.error("上报信息异常 : ", channelFuture.cause());
                }
            }
        });
    }
}
