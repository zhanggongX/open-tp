package cn.opentp.gossip.handler;

import cn.opentp.gossip.GossipManagement;
import cn.opentp.gossip.model.*;
import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AckMessageHandler implements MessageHandler {

    @Override
    public void handle(String cluster, String data, String from) {
        AckMessage ackMessage = JSON.parseObject(data, AckMessage.class);

        List<GossipDigest> olders = ackMessage.getOlders();
        Map<GossipNode, HeartbeatState> newers = ackMessage.getNewers();

        //update local state
        if (newers.size() > 0) {
            GossipManagement.instance().apply2LocalState(newers);
        }

        Map<GossipNode, HeartbeatState> deltaEndpoints = new HashMap<>();
        if (olders != null) {
            for (GossipDigest d : olders) {
                GossipNode member = GossipManagement.instance().createByDigest(d);
                HeartbeatState hb = GossipManagement.instance().endpointMembers().get(member);
                if (hb != null) {
                    deltaEndpoints.put(member, hb);
                }
            }
        }

        if (!deltaEndpoints.isEmpty()) {
            Ack2Message ack2Message = new Ack2Message(deltaEndpoints);
            ByteBuf byteBuf = GossipManagement.instance().encodeAck2Message(ack2Message);
            if (from != null) {
                String[] host = from.split(":");
                GossipManagement.instance().messageService().sendMsg(host[0], Integer.valueOf(host[1]), byteBuf);
            }
        }
    }
}