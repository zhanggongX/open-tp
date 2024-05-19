package cn.opentp.gossip.network.netty;

import cn.opentp.gossip.GossipEnvironment;
import cn.opentp.gossip.message.MessagePayload;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
import cn.opentp.gossip.network.NetworkService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyMessageHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket packet) {
        GossipEnvironment environment = GossipEnvironment.instance();
        NetworkService networkService = environment.networkService();
        if (packet.content() == null) return;
        MessagePayload messagePayload = GossipMessageCodec.codec().decodeMessage(packet.content());
        networkService.handle(messagePayload);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Gossip NetMessageHandler cathe error: ", cause.getCause());
    }
}
