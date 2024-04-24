package cn.opentp.server.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyHttpBootstrap {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ServerBootstrap httpServerBootstrap = new ServerBootstrap();
    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final NioEventLoopGroup workGroup = new NioEventLoopGroup(1);

    private void httpConfig() {
        httpServerBootstrap.group(bossGroup, workGroup).childOption(ChannelOption.SO_BACKLOG, 1024).childOption(ChannelOption.SO_KEEPALIVE, true).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new HttpServerCodec());
                socketChannel.pipeline().addLast(new HttpObjectAggregator(65536));
                socketChannel.pipeline().addLast(new DefaultHttpServerHandler());
            }
        });
    }

    public void start() {
        httpConfig();

        ChannelFuture channelFuture = httpServerBootstrap.bind(8001);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("http server start bind on 8001");
                } else {
                    log.error("http server start error: ", future.cause());
                }
            }
        });
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
