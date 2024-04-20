package cn.opentp.server.net;

import cn.opentp.core.net.handler.OpentpMessageDecoder;
import cn.opentp.core.net.handler.OpentpMessageEncoder;
import cn.opentp.server.net.handler.OpentpHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyBootstrap {

    private static final Logger log = LoggerFactory.getLogger(NettyBootstrap.class);

    public static Thread start() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(new NioEventLoopGroup(10), new NioEventLoopGroup(10))
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                // 20s 收不到心跳，就认为该 channel 断开。
                                socketChannel.pipeline().addLast(new IdleStateHandler(30, 0, 0));
                                socketChannel.pipeline().addLast(new OpentpMessageEncoder());
                                socketChannel.pipeline().addLast(new OpentpMessageDecoder());
                                socketChannel.pipeline().addLast(new OpentpHandler());
                            }
                        });
                serverBootstrap.bind(9527);
            }
        });
        log.info("net server start bind on 9527");
        thread.start();
        return thread;
    }
}
