package cn.opentp.gossip.handler;


import cn.opentp.gossip.GossipManager;
import cn.opentp.gossip.model.AckMessage;
import cn.opentp.gossip.model.GossipDigest;
import cn.opentp.gossip.model.GossipMember;
import cn.opentp.gossip.model.HeartbeatState;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SyncMessageHandler implements MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(SyncMessageHandler.class);

    @Override
    public void handle(String cluster, String data, String from) {
        if (data != null) {
            try {
                JSONArray array = new JSONArray(data);
                List<GossipDigest> olders = new ArrayList<>();
                Map<GossipMember, HeartbeatState> newers = new HashMap<>();
                List<GossipMember> gMemberList = new ArrayList<>();
                for (Object e : array) {
                    GossipDigest g = JSON.parseObject(e.toString(), GossipDigest.class);
//                    GossipDigest g = Serializer.getInstance().decode(Buffer.buffer().appendString(e.toString()), GossipDigest.class);
                    GossipMember member = new GossipMember();
                    member.setCluster(cluster);
                    member.setIpAddress(g.getEndpoint().getAddress().getHostAddress());
                    member.setPort(g.getEndpoint().getPort());
                    member.setId(g.getId());
                    gMemberList.add(member);

                    compareDigest(g, member, cluster, olders, newers);
                }
                // I have, you don't have
                Map<GossipMember, HeartbeatState> endpoints = GossipManager.instance().endpointMembers();
                Set<GossipMember> epKeys = endpoints.keySet();
                for (GossipMember m : epKeys) {
                    if (!gMemberList.contains(m)) {
                        newers.put(m, endpoints.get(m));
                    }
                    if (m.equals(GossipManager.instance().selfNode())) {
                        newers.put(m, endpoints.get(m));
                    }
                }
                AckMessage ackMessage = new AckMessage(olders, newers);
                ByteBuf ackBuffer = GossipManager.instance().encodeAckMessage(ackMessage);
                if (from != null) {
                    String[] host = from.split(":");
                    GossipManager.instance().messageService().sendMsg(host[0], Integer.valueOf(host[1]), ackBuffer);
                }
            } catch (NumberFormatException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void compareDigest(GossipDigest g, GossipMember member, String cluster, List<GossipDigest> olders, Map<GossipMember, HeartbeatState> newers) {

        try {
            HeartbeatState hb = GossipManager.instance().endpointMembers().get(member);
            long remoteHeartbeatTime = g.getHeartbeatTime();
            long remoteVersion = g.getVersion();
            if (hb != null) {
                long localHeartbeatTime = hb.getHeartbeatTime();
                long localVersion = hb.getVersion();

                if (remoteHeartbeatTime > localHeartbeatTime) {
                    olders.add(g);
                } else if (remoteHeartbeatTime < localHeartbeatTime) {
                    newers.put(member, hb);
                } else {
                    if (remoteVersion > localVersion) {
                        olders.add(g);
                    } else if (remoteVersion < localVersion) {
                        newers.put(member, hb);
                    }
                }
            } else {
                olders.add(g);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
