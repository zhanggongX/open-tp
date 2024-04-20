package cn.opentp.client.net;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.client.exception.ServerAddrUnDefineException;
import cn.opentp.client.net.handler.OpentpClientHandler;
import cn.opentp.core.net.handler.OpentpMessageDecoder;
import cn.opentp.core.net.handler.OpentpMessageEncoder;
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

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NettyBootstrap {

    private final static Logger log = LoggerFactory.getLogger(NettyBootstrap.class);

    public static void start() {

        Bootstrap clientBootstrap = new Bootstrap();

        clientBootstrap.group(new NioEventLoopGroup(1))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new IdleStateHandler(10, 0, 0));
                        socketChannel.pipeline().addLast(new OpentpMessageEncoder());
                        socketChannel.pipeline().addLast(new OpentpMessageDecoder());
                        socketChannel.pipeline().addLast(new OpentpClientHandler());
                    }
                });
        // 客户端
        Configuration.configuration().setBootstrap(clientBootstrap);

        // 配置的服务器信息
        List<InetSocketAddress> inetSocketAddresses = Configuration.configuration().serverAddresses();
        if (inetSocketAddresses.isEmpty()) {
            throw new ServerAddrUnDefineException();
        }
        // todo 集群迭代。
        log.debug("服务端地址：{}, 端口：{}", inetSocketAddresses.get(0).getHostName(), inetSocketAddresses.get(0).getPort());
        ChannelFuture channelFuture = clientBootstrap.connect(inetSocketAddresses.get(0));

        // 链接成功回调
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    // todo 发送权限验证
                    // 记录 channel
                    Configuration.configuration().setThreadPoolReportChannel(channelFuture.channel());
                    // 没一秒都去上报 todo 配置
                    channelFuture.channel().eventLoop().scheduleAtFixedRate(new ThreadPoolStateReportThread(), 1, 1, TimeUnit.SECONDS);
                } else {
                    // 重试
                    Throwable cause = channelFuture.cause();
                    log.error("链接失败：{}", cause.toString());
                }
            }
        });
    }
}
