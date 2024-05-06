package cn.opentp.server.transport.keepr;

import cn.opentp.server.OpentpApp;
import cn.opentp.server.transport.TransportClient;
import io.netty.channel.Channel;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 与服务端的连接保持
 * 为了方便定制不同重连策略所以没有使用 EventLoop 的 scheduleAtFixedRate
 */
public class TransportClientKeeperTask implements Runnable {

    private final TransportClient transportClient;
    private final OpentpApp opentpApp = OpentpApp.instance();

    public TransportClientKeeperTask(TransportClient transportClient) {
        this.transportClient = transportClient;
    }

    public static void startup(TransportClient transportClient) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 30s 重试
        scheduledExecutorService.scheduleAtFixedRate(new TransportClientKeeperTask(transportClient), 30, 30, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        List<SocketAddress> socketAddresses = opentpApp.clusterFailConnects();
        if (socketAddresses.isEmpty()) {
            return;
        }
        transportClient.doConnect(true);
    }
}
