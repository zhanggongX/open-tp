package cn.opentp.gossip.message.handler;


import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.message.AckMessage;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
import cn.opentp.gossip.node.GossipNodeContext;
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
        if (data == null) return;

        try {
            List<GossipNodeDigest> gossipNodeDigests = JSON.parseArray(data, GossipNodeDigest.class);

            List<GossipNodeDigest> oldNodes = new ArrayList<>();
            Map<GossipNode, HeartbeatState> newNodes = new HashMap<>();

            List<GossipNode> syncedNodes = new ArrayList<>();
            for (GossipNodeDigest gossipNodeDigest : gossipNodeDigests) {
                GossipNode node = new GossipNode();
                node.setCluster(cluster);
                node.setHost(gossipNodeDigest.getSocketAddress().getAddress().getHostAddress());
                node.setPort(gossipNodeDigest.getSocketAddress().getPort());
                node.setNodeId(gossipNodeDigest.getNodeId());
                syncedNodes.add(node);

                compareDigest(gossipNodeDigest, node, cluster, oldNodes, newNodes);
            }

            GossipNodeContext nodeContext = GossipApp.instance().gossipNodeContext();
            // 把本节点有的，其他节点没有的集群节点，通过 ack 同步回去
            Map<GossipNode, HeartbeatState> endpoints = nodeContext.endpointNodes();
            Set<GossipNode> epKeys = endpoints.keySet();
            for (GossipNode m : epKeys) {
                if (!syncedNodes.contains(m)) {
                    newNodes.put(m, endpoints.get(m));
                }
                if (m.equals(GossipApp.instance().selfNode())) {
                    newNodes.put(m, endpoints.get(m));
                }
            }
            AckMessage ackMessage = new AckMessage(oldNodes, newNodes);
            ByteBuf ackBuffer = GossipMessageCodec.codec().encodeAckMessage(ackMessage);
            if (from != null) {
                String[] host = from.split(":");
                GossipApp.instance().networkService().send(host[0], Integer.valueOf(host[1]), ackBuffer);
            }
        } catch (NumberFormatException e) {
            log.error(e.getMessage());
        }
    }

    private void compareDigest(GossipNodeDigest gossipNodeDigest, GossipNode syncedNode, String cluster, List<GossipNodeDigest> oldNodes, Map<GossipNode, HeartbeatState> newNodes) {

        try {
            // 同步过来的节点的信息
            long remoteHeartbeatTime = gossipNodeDigest.getHeartbeatTime();
            long remoteVersion = gossipNodeDigest.getVersion();

            GossipNodeContext nodeContext = GossipApp.instance().gossipNodeContext();
            HeartbeatState heartbeatState = nodeContext.endpointNodes().get(syncedNode);
            if (heartbeatState != null) {
                long localHeartbeatTime = heartbeatState.getHeartbeatTime();
                long localVersion = heartbeatState.getVersion();

                if (remoteHeartbeatTime > localHeartbeatTime) {
                    oldNodes.add(gossipNodeDigest);
                } else if (remoteHeartbeatTime < localHeartbeatTime) {
                    newNodes.put(syncedNode, heartbeatState);
                } else {
                    if (remoteVersion > localVersion) {
                        oldNodes.add(gossipNodeDigest);
                    } else if (remoteVersion < localVersion) {
                        newNodes.put(syncedNode, heartbeatState);
                    }
                }
            } else {
                oldNodes.add(gossipNodeDigest);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
