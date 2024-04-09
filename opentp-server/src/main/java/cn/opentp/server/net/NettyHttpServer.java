package cn.opentp.server.net;

import cn.opentp.core.net.handler.ThreadPoolWrapperDecoder;
import cn.opentp.core.net.handler.ThreadPoolWrapperEncoder;
import cn.opentp.server.net.handler.DefaultHttpServerHandler;
import cn.opentp.server.net.handler.DefaultServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
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
                                socketChannel.pipeline().addLast(new HttpRequestDecoder());
                                socketChannel.pipeline().addLast(new HttpResponseEncoder());
                                socketChannel.pipeline().addLast(new DefaultHttpServerHandler());
                            }
                        });
                httpServerBootstrap.bind(8001);
            }
        });
        log.info("http server start bind on 8001");
        thread.start();
        return thread;
    }
}
