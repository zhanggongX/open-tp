package cn.opentp.gossip.message.handler;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.message.Ack2Message;
import cn.opentp.gossip.message.AckMessage;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
import cn.opentp.gossip.model.*;
import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AckMessageHandler extends AbstractMessageHandler implements MessageHandler {

    @Override
    public void handle(String cluster, String data, String from) {
        AckMessage ackMessage = JSON.parseObject(data, AckMessage.class);

        List<GossipDigest> olders = ackMessage.getOlders();
        Map<GossipNode, HeartbeatState> newers = ackMessage.getNewers();

        //update local state
        if (!newers.isEmpty()) {
            apply2LocalState(newers);
        }

        Map<GossipNode, HeartbeatState> deltaEndpoints = new HashMap<>();
        if (olders != null) {
            for (GossipDigest d : olders) {
                GossipNode member = createByDigest(d);
                HeartbeatState hb = GossipApp.instance().endpointNodeCache().get(member);
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
                GossipApp.instance().networkService().send(host[0], Integer.valueOf(host[1]), byteBuf);
            }
        }
    }

    public GossipNode createByDigest(GossipDigest digest) {
        GossipNode member = new GossipNode();
        member.setPort(digest.getEndpoint().getPort());
        member.setHost(digest.getEndpoint().getAddress().getHostAddress());
        member.setCluster(GossipApp.instance().setting().getCluster());

        Set<GossipNode> keys = GossipApp.instance().endpointNodeCache().keySet();
        for (GossipNode m : keys) {
            if (m.equals(member)) {
                member.setNodeId(m.getNodeId());
                member.setState(m.getState());
                break;
            }
        }

        return member;
    }
}
