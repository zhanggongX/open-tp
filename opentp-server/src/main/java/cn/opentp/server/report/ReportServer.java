package cn.opentp.server.report;

import cn.opentp.core.net.handler.OpentpMessageDecoder;
import cn.opentp.core.net.handler.OpentpMessageEncoder;
import cn.opentp.server.configuration.Configuration;
import cn.opentp.server.constant.Constant;
import cn.opentp.server.report.handler.ReportServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public class ReportServer implements Closeable {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();
    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup(2);
    private final NioEventLoopGroup workGroup = new NioEventLoopGroup(5);

    private void configServer() {

        serverBootstrap.group(bossGroup, workGroup)
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
                        socketChannel.pipeline().addLast(new ReportServerHandler());
                    }
                });
    }

    public void start(String bindPort) {
        int port = bindPort == null ? Constant.DEFAULT_REPORT_SERVER_PORT : Integer.parseInt(bindPort);

        configServer();
        serverBootstrap.bind(port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("report server start bind on {}", port);
                } else {
                    log.error("report server start error: ", future.cause());
                }
            }
        });
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
