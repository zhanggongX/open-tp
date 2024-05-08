package cn.opentp.gossip.net.udp;


import cn.opentp.gossip.handler.*;
import cn.opentp.gossip.model.MessageType;
import cn.opentp.gossip.net.MsgService;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDPMsgService implements MsgService {

    private Logger log = LoggerFactory.getLogger(UDPMsgService.class);

    private NioDatagramChannel socket;
    private EventLoopGroup eventLoopGroup;

    @Override
    public void listen(String ipAddress, int port) {
        eventLoopGroup = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)    //广播
                .option(ChannelOption.SO_RCVBUF, 2048 * 1024)// 设置UDP读缓冲区为2M
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024)// 设置UDP写缓冲区为1M
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        // todo
                    }
                });

        ChannelFuture channelFuture = b.bind(7397);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("Socket bind success!");
                } else {
                    log.error("An error occurred while bind the socket: ", future.cause());
                }
            }
        });
    }

    @Override
    public void handleMsg(ByteBuf data) {
//        JsonObject j = data.toJsonObject();
//        String msgType = j.getString(GossipMessageFactory.KEY_MSG_TYPE);
//        String _data = j.getString(GossipMessageFactory.KEY_DATA);
//        String cluster = j.getString(GossipMessageFactory.KEY_CLUSTER);
//        String from = j.getString(GossipMessageFactory.KEY_FROM);
//        if ((null == cluster || cluster.isEmpty()) || !GossipManager.getInstance().getCluster().equals(cluster)) {
//            log.error("This message shouldn't exist in my world!" + data);
//            return;
//        }

        String msgType = "";
        String cluster = "";
        String _data = "";
        String from = "";

        MessageHandler handler = null;
        MessageType type = MessageType.valueOf(msgType);
        if (type == MessageType.SYNC_MESSAGE) {
            handler = new SyncMessageHandler();
        } else if (type == MessageType.ACK_MESSAGE) {
            handler = new AckMessageHandler();
        } else if (type == MessageType.ACK2_MESSAGE) {
            handler = new Ack2MessageHandler();
        } else if (type == MessageType.SHUTDOWN) {
            handler = new ShutdownMessageHandler();
        } else if (type == MessageType.REG_MESSAGE) {
            handler = new RegularMessageHandler();
        } else {
            log.error("Not supported message type");
        }
        if (handler != null) {
            handler.handle(cluster, _data, from);
        }
    }

    @Override
    public void sendMsg(String targetIp, Integer targetPort, ByteBuf data) {
//        DatagramPacket datagramPacket = new DatagramPacket(data, packet.sender());
//        socket.writeAndFlush(datagramPacket);
    }

    @Override
    public void unListen() {
        eventLoopGroup.shutdownGracefully();
        ChannelFuture channelFuture = socket.close();
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
