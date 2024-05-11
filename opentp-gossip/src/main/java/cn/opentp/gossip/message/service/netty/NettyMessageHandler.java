package cn.opentp.gossip.message.service.netty;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.message.service.MessageService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class NettyMessageHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket packet) throws Exception {
        GossipApp management = GossipApp.instance();
        MessageService messageService = management.messageService();
        if (packet.content() == null) return;
        String data = packet.content().toString(StandardCharsets.UTF_8);
        messageService.handle(data);
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("NetMessageHandler cathe error: ", cause.getCause());
    }
}
