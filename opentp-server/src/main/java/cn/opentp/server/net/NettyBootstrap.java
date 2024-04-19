package cn.opentp.server.net;

import cn.opentp.core.net.handler.ThreadPoolStateDecoder;
import cn.opentp.core.net.handler.ThreadPoolStateEncoder;
import cn.opentp.server.net.handler.OpentpHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast(new ThreadPoolStateEncoder());
                                socketChannel.pipeline().addLast(new ThreadPoolStateDecoder());
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
