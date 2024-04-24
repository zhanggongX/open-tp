package cn.opentp.server.rest;

import cn.opentp.server.constant.Constant;
import cn.opentp.server.rest.handler.RestServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestServer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();
    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final NioEventLoopGroup workGroup = new NioEventLoopGroup(1);

    private void httpConfig() {
        serverBootstrap.group(bossGroup, workGroup)
                .childOption(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new HttpServerCodec());
                        socketChannel.pipeline().addLast(new HttpObjectAggregator(65536));
                        socketChannel.pipeline().addLast(new RestServerHandler());
                    }
                });
    }

    public void start(String bindPort) {
        int port = bindPort == null ? Constant.DEFAULT_REST_SERVER_PORT : Integer.parseInt(bindPort);

        httpConfig();

        ChannelFuture channelFuture = serverBootstrap.bind(port);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("http rest api server start bind on {}", port);
                } else {
                    log.error("http rest server start error: ", future.cause());
                }
            }
        });
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
