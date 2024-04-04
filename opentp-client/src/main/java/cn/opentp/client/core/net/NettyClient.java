package cn.opentp.client.core.net;

import cn.opentp.client.core.net.handler.DemoHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class NettyClient {
    private final static Logger log = LoggerFactory.getLogger(NettyClient.class);
    private static final List<ChannelFuture> channelFutures = new ArrayList<>();

    public static void send(ByteBuf msg) {
        if(channelFutures.isEmpty()){
            return;
        }
        channelFutures.get(0).channel().writeAndFlush(msg);
    }

    public static Thread start() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Bootstrap clientBootstrap = new Bootstrap();
                clientBootstrap.group(new NioEventLoopGroup(10))
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast(new DemoHandler());
                            }
                        });
                ChannelFuture channelFuture = null;
                try {
                    channelFuture = clientBootstrap.connect("localhost", 9527).sync();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                channelFutures.add(channelFuture);
                System.out.println(1);
            }
        });
        log.info("net server start bind on 9527");
        thread.start();
        return thread;
    }
}
