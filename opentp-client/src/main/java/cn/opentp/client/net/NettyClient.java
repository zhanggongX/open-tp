package cn.opentp.client.net;

import cn.opentp.client.net.handler.DefaultClientHandler;
import cn.opentp.core.net.handler.ThreadPoolWrapperDecoder;
import cn.opentp.core.net.handler.ThreadPoolWrapperEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class NettyClient {
    private final static Logger log = LoggerFactory.getLogger(NettyClient.class);
    private static final List<ChannelFuture> channelFutures = new ArrayList<>();

    public static void send(ByteBuf msg) {
        if(channelFutures.isEmpty()){
            return;
        }
        channelFutures.get(0).channel().writeAndFlush(msg);
    }

    public static void send(Object o) {
        if(channelFutures.isEmpty()){
            return;
        }
        channelFutures.get(0).channel().writeAndFlush(o);
    }

    public static void send(byte[] msg) {
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
                                socketChannel.pipeline().addLast(new ThreadPoolWrapperEncoder());
                                socketChannel.pipeline().addLast(new ThreadPoolWrapperDecoder());
                                socketChannel.pipeline().addLast(new DefaultClientHandler());
                            }
                        });
                ChannelFuture channelFuture = clientBootstrap.connect("localhost", 9527);
                // 链接成功回调
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if(channelFuture.isSuccess()){
                            // todo 发送权限验证
                            channelFutures.add(channelFuture);
                        }else{
                            Throwable cause = channelFuture.cause();
                            log.error("链接失败：{}", cause.toString());
                        }

                    }
                });
                channelFutures.add(channelFuture);
                System.out.println(1);
            }
        });
        log.info("net server start bind on 9527");
        thread.start();
        return thread;
    }
}
