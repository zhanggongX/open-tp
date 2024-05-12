package cn.opentp.gossip.message.handler;


import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.message.AckMessage;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
import cn.opentp.gossip.node.GossipNodeDigest;
import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.node.HeartbeatState;
import com.alibaba.fastjson2.JSON;
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
                List<GossipNodeDigest> gossipDigests = JSON.parseArray(data, GossipNodeDigest.class);

                List<GossipNodeDigest> olders = new ArrayList<>();
                Map<GossipNode, HeartbeatState> newers = new HashMap<>();

                List<GossipNode> gMemberList = new ArrayList<>();
                for (GossipNodeDigest gossipDigest : gossipDigests) {
//                    GossipDigest g = Serializer.getInstance().decode(Buffer.buffer().appendString(e.toString()), GossipDigest.class);
                    GossipNode member = new GossipNode();
                    member.setCluster(cluster);
                    member.setHost(gossipDigest.getSocketAddress().getAddress().getHostAddress());
                    member.setPort(gossipDigest.getSocketAddress().getPort());
                    member.setNodeId(gossipDigest.getNodeId());
                    gMemberList.add(member);

                    compareDigest(gossipDigest, member, cluster, olders, newers);
                }
                // I have, you don't have
                Map<GossipNode, HeartbeatState> endpoints = GossipApp.instance().gossipNodeContext().endpointNodes();
                Set<GossipNode> epKeys = endpoints.keySet();
                for (GossipNode m : epKeys) {
                    if (!gMemberList.contains(m)) {
                        newers.put(m, endpoints.get(m));
                    }
                    if (m.equals(GossipApp.instance().selfNode())) {
                        newers.put(m, endpoints.get(m));
                    }
                }
                AckMessage ackMessage = new AckMessage(olders, newers);
                ByteBuf ackBuffer = GossipMessageCodec.codec().encodeAckMessage(ackMessage);
                if (from != null) {
                    String[] host = from.split(":");
                    GossipApp.instance().networkService().send(host[0], Integer.valueOf(host[1]), ackBuffer);
                }
            } catch (NumberFormatException e) {
                log.error(e.getMessage());
            }
        }
    }

    private void compareDigest(GossipNodeDigest g, GossipNode member, String cluster, List<GossipNodeDigest> olders, Map<GossipNode, HeartbeatState> newers) {

        try {
            HeartbeatState hb = GossipApp.instance().gossipNodeContext().endpointNodes().get(member);
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
