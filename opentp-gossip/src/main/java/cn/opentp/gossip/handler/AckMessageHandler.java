package cn.opentp.gossip.handler;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.message.Ack2Message;
import cn.opentp.gossip.message.AckMessage;
import cn.opentp.gossip.message.GossipMessageCodec;
import cn.opentp.gossip.model.*;
import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AckMessageHandler implements MessageHandler {

    @Override
    public void handle(String cluster, Object data, String from) {
        AckMessage ackMessage = (AckMessage) data;

        List<GossipDigest> olders = ackMessage.getOlders();
        Map<GossipNode, HeartbeatState> newers = ackMessage.getNewers();

        //update local state
        if (newers.size() > 0) {
            GossipApp.instance().apply2LocalState(newers);
        }

        Map<GossipNode, HeartbeatState> deltaEndpoints = new HashMap<>();
        if (olders != null) {
            for (GossipDigest d : olders) {
                GossipNode member = GossipApp.instance().createByDigest(d);
                HeartbeatState hb = GossipApp.instance().endpointMembers().get(member);
                if (hb != null) {
                    deltaEndpoints.put(member, hb);
                }
            }
        }

        if (!deltaEndpoints.isEmpty()) {
            Ack2Message ack2Message = new Ack2Message(deltaEndpoints);
            ByteBuf byteBuf = GossipMessageCodec.codec().encodeAck2Message(ack2Message);
            if (from != null) {
                String[] host = from.split(":");
                GossipApp.instance().messageService().send(host[0], Integer.valueOf(host[1]), byteBuf);
            }
        }
    }
}
