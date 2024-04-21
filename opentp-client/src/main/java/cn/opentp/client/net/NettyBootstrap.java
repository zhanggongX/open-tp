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

    private final Bootstrap clientBootstrap = new Bootstrap();
    private final NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(1);

    public void configBootstrap() {

        clientBootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // 每五秒一直没有发送任何消息，则发生一个心跳
                        socketChannel.pipeline().addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        socketChannel.pipeline().addLast(new OpentpMessageEncoder());
                        socketChannel.pipeline().addLast(new OpentpMessageDecoder());
                        socketChannel.pipeline().addLast(new OpentpClientHandler());
                    }
                });
    }

    public void doConnect() {
        // 配置的服务器信息
        List<InetSocketAddress> inetSocketAddresses = Configuration.configuration().serverAddresses();
        if (inetSocketAddresses.isEmpty()) {
            throw new ServerAddrUnDefineException();
        }
        // todo 集群迭代。
        log.debug("服务端地址：{}, 端口：{}", inetSocketAddresses.get(0).getHostName(), inetSocketAddresses.get(0).getPort());
        log.debug("尝试连接服务器...");
        ChannelFuture channelFuture = clientBootstrap.connect(inetSocketAddresses.get(0));

        // 监听连接状态
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    // todo 发送权限验证
                    // 记录 channel
                    Configuration.configuration().threadPoolStateReportChannel(channelFuture.channel());
                }
            }
        });
    }

    public void startup() {
        // 配置 netty 引导类
        configBootstrap();
        // 连接服务器
        doConnect();
    }

    public void shutdown() {
        nioEventLoopGroup.shutdownGracefully();
    }
}
