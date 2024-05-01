package cn.opentp.server.transport;

import cn.opentp.server.transport.codec.BroadcastMessageDecoder;
import cn.opentp.server.transport.codec.BroadcastMessageEncoder;
import cn.opentp.server.transport.handler.TransportServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public class TransportServer implements Closeable {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();
    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final NioEventLoopGroup workGroup = new NioEventLoopGroup(1);

    private void httpConfig() {

        serverBootstrap.group(bossGroup, workGroup)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new BroadcastMessageDecoder());
                        socketChannel.pipeline().addLast(new BroadcastMessageEncoder());
                        socketChannel.pipeline().addLast(new TransportServerHandler());
                    }
                });
    }

    public void start(int bindPort) {
        httpConfig();

        ChannelFuture channelFuture = serverBootstrap.bind(bindPort);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("transport server start bind on {}", bindPort);
                } else {
                    log.error("transport server start error: ", future.cause());
                }
            }
        });
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
