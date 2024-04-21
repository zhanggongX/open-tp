package cn.opentp.client.net.keepr;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.client.net.NettyBootstrap;
import io.netty.channel.Channel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 与服务端的连接保持
 * 为了方便定制不同重连策略所以没有使用 EventLoop 的 scheduleAtFixedRate
 */
public class NettyConnectKeeperTask implements Runnable {

    private final NettyBootstrap nettyBootstrap;

    public NettyConnectKeeperTask(NettyBootstrap nettyBootstrap) {
        this.nettyBootstrap = nettyBootstrap;
    }

    public static void startup(NettyBootstrap nettyBootstrap) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new NettyConnectKeeperTask(nettyBootstrap), 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        Channel channel = Configuration.configuration().threadPoolStateReportChannel();
        if (channel != null && channel.isActive()) {
            return;
        }
        // todo 不同的重连策略
        nettyBootstrap.doConnect();
    }
}
