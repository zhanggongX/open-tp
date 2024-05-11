package cn.opentp.gossip.network.netty;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.network.NetworkService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class NettyMessageHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket packet) {
        GossipApp management = GossipApp.instance();
        NetworkService networkService = management.networkService();
        if (packet.content() == null) return;
        String data = packet.content().toString(StandardCharsets.UTF_8);
        networkService.handle(data);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("NetMessageHandler cathe error: ", cause.getCause());
    }
}
