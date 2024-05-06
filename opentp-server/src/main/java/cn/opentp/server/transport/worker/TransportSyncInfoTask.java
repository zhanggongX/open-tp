package cn.opentp.server.transport.worker;

import cn.opentp.server.OpentpApp;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Collection;
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
public class TransportSyncInfoTask implements Runnable {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final OpentpApp opentpApp = OpentpApp.instance();

    public static void startup() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new TransportSyncInfoTask(), 10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        Map<SocketAddress, Channel> clusterConnected = opentpApp.clusterConnected();

        Collection<Channel> clusterChannel = clusterConnected.values();
        for (Channel channel : clusterChannel) {
            // todo 上报当前服务器的所有客户端信息
//            List<ThreadPoolState> threadPoolStates = new ArrayList<>();
//
//            Map<String, ThreadPoolContext> threadPoolContextCache = Configuration.configuration().threadPoolContextCache();
//            for (Map.Entry<String, ThreadPoolContext> threadPoolContextEntry : threadPoolContextCache.entrySet()) {
//                String threadPoolKey = threadPoolContextEntry.getKey();
//                ThreadPoolContext threadPoolContext = threadPoolContextEntry.getValue();
//                threadPoolContext.flushStateAndSetThreadPoolName(threadPoolKey);
//                threadPoolStates.add(threadPoolContext.getState());
//            }
//
//            // 克隆信息，自带魔数和版本号
//            OpentpMessage opentpMessage = OpentpCoreConstant.OPENTP_MSG_PROTO.clone();
//            OpentpMessage.builder().messageType(OpentpMessageTypeEnum.THREAD_POOL_EXPORT.getCode()).serializerType(OpentpMessageTypeEnum.THREAD_POOL_EXPORT.getCode()).traceId(MessageTraceIdUtil.traceId()).licenseKey(channel.attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).get()).data(threadPoolStates).buildTo(opentpMessage);
//
//            ChannelFuture channelFuture = channel.writeAndFlush(opentpMessage);
//            channelFuture.addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                    if (channelFuture.isSuccess()) {
//                        log.debug("上报线程消息成功");
//                    } else {
//                        log.error("上报信息异常 : ", channelFuture.cause());
//                    }
//                }
//            });
        }
    }
}
