package cn.opentp.gossip.network;

import cn.opentp.core.util.JSONUtils;
import cn.opentp.core.util.JacksonUtil;
import cn.opentp.gossip.enums.MessageTypeEnum;
import cn.opentp.gossip.message.MessagePayload;
import cn.opentp.gossip.message.handler.*;
import cn.opentp.gossip.network.netty.NettyMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class UDPNetworkService implements NetworkService {

    private final Logger log = LoggerFactory.getLogger(UDPNetworkService.class);

    private Channel channel;
    private EventLoopGroup eventLoopGroup;

    @Override
    public void start(String host, int port) {

        eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, false)
                .option(ChannelOption.SO_RCVBUF, 2048 * 1024)
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new NettyMessageHandler());
                    }
                });

        ChannelFuture channelFuture = bootstrap.bind(host, port);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("Socket bind success!");
                    channel = future.channel();
                } else {
                    log.error("An error occurred while bind the socket: ", future.cause());
                }
            }
        });
    }

    /**
     * 处理消息
     *
     * @param data 消息内容
     */
    @Override
    public void handle(String data) {
        log.trace("处理消息：{}", data);
        MessagePayload gossipMessage = JSONUtils.fromJson(data, MessagePayload.class);

        MessageHandler handler = null;
        MessageTypeEnum type = MessageTypeEnum.parse(gossipMessage.getType());
        if (type == MessageTypeEnum.SYNC) {
            handler = new SyncMessageHandler();
        } else if (type == MessageTypeEnum.ACK) {
            handler = new AckMessageHandler();
        } else if (type == MessageTypeEnum.ACK2) {
            handler = new Ack2MessageHandler();
        } else if (type == MessageTypeEnum.SHUTDOWN) {
            handler = new ShutdownMessageHandler();
        } else if (type == MessageTypeEnum.GOSSIP) {
            handler = new GossipMessageHandler();
        } else {
            log.error("Not supported message type");
        }

        if (handler != null) {
            handler.handle(gossipMessage.getCluster(), gossipMessage.getData(), gossipMessage.getFrom());
        }
    }

    @Override
    public void send(String targetHost, Integer targetPort, Object message) {
        if (message instanceof ByteBuf sendBuf) {
            // ps： 由于消息会多次发送，这里发送的是 copy 的信息
            // 所以发送方要记得 release() 消息
            ByteBuf realSendBuf = sendBuf.copy();
            log.trace("发送消息：{}", realSendBuf.toString(StandardCharsets.UTF_8));
            DatagramPacket datagramPacket = new DatagramPacket(realSendBuf, new InetSocketAddress(targetHost, targetPort));
            channel.writeAndFlush(datagramPacket);
        } else {
            String json = JSONUtils.toJSONString(message);
            DatagramPacket datagramPacket = new DatagramPacket(Unpooled.copiedBuffer(json, StandardCharsets.UTF_8), new InetSocketAddress(targetHost, targetPort));
            channel.writeAndFlush(datagramPacket);
        }
    }

    @Override
    public void close() {
        eventLoopGroup.shutdownGracefully();
        ChannelFuture channelFuture = channel.close();
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("Socket was close!");
                } else {
                    log.error("An error occurred while closing the socket: ", future.cause());
                }
            }
        });
    }
}
