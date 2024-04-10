package cn.opentp.server.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyHttpServer {
    private static final Logger log = LoggerFactory.getLogger(NettyHttpServer.class);

    public static Thread start() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerBootstrap httpServerBootstrap = new ServerBootstrap();
                httpServerBootstrap.group(new NioEventLoopGroup(10), new NioEventLoopGroup(10))
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast(new HttpServerCodec());
                                socketChannel.pipeline().addLast(new HttpObjectAggregator(65536));
                                socketChannel.pipeline().addLast(new DefaultHttpServerHandler());
                            }
                        });
                try {
                    ChannelFuture channelFuture = httpServerBootstrap.bind(8001).sync();
                    Channel channel = channelFuture.channel();
                    channel.closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        log.info("http server start bind on 8001");
        thread.start();
        return thread;
    }
}
