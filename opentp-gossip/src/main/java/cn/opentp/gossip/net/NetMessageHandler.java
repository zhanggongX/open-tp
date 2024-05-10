package cn.opentp.gossip.net;

import cn.opentp.gossip.GossipManagement;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.nio.charset.StandardCharsets;

public class NetMessageHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket packet) throws Exception {
        GossipManagement management = GossipManagement.instance();
        MessageService messageService = management.messageService();
        String msg = packet.content().toString(StandardCharsets.UTF_8);
        System.out.println(msg);
        messageService.handle(packet.content());
    }
}
