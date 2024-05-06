package cn.opentp.server.transport.handler;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.auth.ServerInfo;
import cn.opentp.core.net.BroadcastMessage;
import cn.opentp.server.OpentpApp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class TransportServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof BroadcastMessage broadcastMessage) {
                channelRead0(ctx, broadcastMessage);
            } else {
                log.warn("未知消息类型，丢弃！");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void channelRead0(ChannelHandlerContext ctx, BroadcastMessage broadcastMessage) {
        OpentpApp configuration = OpentpApp.instance();
        Map<ClientInfo, ServerInfo> clusterClientInfoCache = configuration.clusterClientInfoCache();
        Map<ServerInfo, List<ClientInfo>> clusterServerInfoCache = configuration.clusterServerInfoCache();

        List<ClientInfo> clientInfos = broadcastMessage.getClientInfos();
        
    }
}
