package cn.opentp.client.net.worker;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.core.thread.pool.ThreadPoolContext;
import cn.opentp.core.util.MessageTraceIdUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static void startup() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new ThreadPoolStateExportTask(), 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        Channel channel = Configuration.configuration().threadPoolStateReportChannel();
        if (channel == null || !channel.isActive()) {
            return;
        }

        Map<String, ThreadPoolContext> threadPoolContextCache = Configuration.configuration().threadPoolContextCache();
        for (Map.Entry<String, ThreadPoolContext> threadPoolContextEntry : threadPoolContextCache.entrySet()) {
            String threadPoolKey = threadPoolContextEntry.getKey();
            ThreadPoolContext threadPoolContext = threadPoolContextEntry.getValue();
            threadPoolContext.flushStateAndSetThreadPoolName(threadPoolKey);

            // 克隆信息，自带魔数和版本号
            OpentpMessage opentpMessage = Configuration.OPENTP_MSG_PROTO.clone();
            OpentpMessage
                    .builder()
                    .messageType(OpentpMessageTypeEnum.THREAD_POOL_EXPORT.getCode())
                    .serializerType(OpentpMessageTypeEnum.THREAD_POOL_EXPORT.getCode())
                    .traceId(MessageTraceIdUtil.traceId())
                    .data(threadPoolContext.getState())
                    .buildTo(opentpMessage);

            // todo 批量上报
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
}
