package cn.opentp.gossip.handler;

import cn.opentp.gossip.core.GossipManager;
import cn.opentp.gossip.model.*;
import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;

import java.nio.Buffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AckMessageHandler implements MessageHandler {

    @Override
    public void handle(String cluster, String data, String from) {
        AckMessage ackMessage = JSON.parseObject(data, AckMessage.class);

        List<GossipDigest> olders = ackMessage.getOlders();
        Map<GossipMember, HeartbeatState> newers = ackMessage.getNewers();

        //update local state
        if (newers.size() > 0) {
            GossipManager.getInstance().apply2LocalState(newers);
        }

        Map<GossipMember, HeartbeatState> deltaEndpoints = new HashMap<>();
        if (olders != null) {
            for (GossipDigest d : olders) {
                GossipMember member = GossipManager.getInstance().createByDigest(d);
                HeartbeatState hb = GossipManager.getInstance().getEndpointMembers().get(member);
                if (hb != null) {
                    deltaEndpoints.put(member, hb);
                }
            }
        }

        if (!deltaEndpoints.isEmpty()) {
            Ack2Message ack2Message = new Ack2Message(deltaEndpoints);
            ByteBuf byteBuf = GossipManager.getInstance().encodeAck2Message(ack2Message);
            if (from != null) {
                String[] host = from.split(":");
                GossipManager.getInstance().getSettings().getMsgService().sendMsg(host[0], Integer.valueOf(host[1]), byteBuf);
            }
        }
    }
}
