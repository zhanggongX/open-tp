package cn.opentp.client.net;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.core.thread.pool.ThreadPoolContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;


public class ThreadPoolStateReportThread implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(ThreadPoolStateReportThread.class);

    @Override
    public void run() {
        Channel channel = Configuration.configuration().threadPoolReportChannel();

        Map<String, ThreadPoolContext> threadPoolContextCache = Configuration.configuration().threadPoolContextCache();
        for (Map.Entry<String, ThreadPoolContext> threadPoolContextEntry : threadPoolContextCache.entrySet()) {
            String threadPoolKey = threadPoolContextEntry.getKey();
            ThreadPoolContext threadPoolContext = threadPoolContextEntry.getValue();
            threadPoolContext.flushState(threadPoolKey);
            ChannelFuture channelFuture = channel.writeAndFlush(threadPoolContext.getState());
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    log.info("发送消息结果： {}", channelFuture.isSuccess());
                    if (!channelFuture.isSuccess()) {
                        Throwable cause = channelFuture.cause();
                        cause.printStackTrace();
                    }
                }
            });

            // todo 重试策略优化
            if (!channel.isActive()) {
                Bootstrap bootstrap = Configuration.configuration().bootstrap();
                List<InetSocketAddress> inetSocketAddresses = Configuration.configuration().serverAddresses();
                InetSocketAddress inetSocketAddress = inetSocketAddresses.get(0);
                ChannelFuture reChannelFuture = bootstrap.connect(inetSocketAddress);
                reChannelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        Configuration.configuration().setThreadPoolReportChannel(reChannelFuture.channel());
                    }
                });
            }
        }
        // todo 批量上报
        // channel.writeAndFlush(threadPoolContextCache.values());
    }
}
