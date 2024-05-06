package cn.opentp.server.transport;

import cn.opentp.core.net.codec.OpentpMessageDecoder;
import cn.opentp.core.net.codec.OpentpMessageEncoder;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.OpentpProperties;
import cn.opentp.server.transport.handler.TransportClientHandler;
import cn.opentp.server.transport.keepr.TransportClientKeeperTask;
import cn.opentp.server.transport.worker.TransportSyncInfoTask;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TransportClient implements Closeable {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Bootstrap bootstrap = new Bootstrap();
    private final OpentpApp opentpApp = OpentpApp.instance();
    private final OpentpProperties properties = opentpApp.properties();

    private NioEventLoopGroup workGroup = null;

    public void transportClientConfig() {
        List<SocketAddress> clusterSocketAddresses = properties.getCluster();

        workGroup = new NioEventLoopGroup(clusterSocketAddresses.size());
        bootstrap.group(workGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                // 每五秒一直没有发送任何消息，则发生一个心跳
                socketChannel.pipeline().addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                socketChannel.pipeline().addLast(new OpentpMessageEncoder());
                socketChannel.pipeline().addLast(new OpentpMessageDecoder());
                socketChannel.pipeline().addLast(new TransportClientHandler());
            }
        });
    }

    public void doConnect(boolean started) {

        List<SocketAddress> clusterSocketAddresses = properties.getCluster();

        for (SocketAddress socketAddress : clusterSocketAddresses) {
            ChannelFuture channelFuture = bootstrap.connect(socketAddress);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("连接：{} 成功", socketAddress);
                    } else {
                        log.info("连接：{} 失败，稍后将重试", socketAddress);
                    }
                }
            });
        }

        if (!started) {
            // 连接保持器
            TransportClientKeeperTask.startup(this);
            // 线程信息上报
            TransportSyncInfoTask.startup();
        }

    }

    public void startup() {
        // 配置 netty 引导类
        transportClientConfig();
        // 连接服务器
        doConnect(false);
    }

    public void close() {
        workGroup.shutdownGracefully();
    }
}
