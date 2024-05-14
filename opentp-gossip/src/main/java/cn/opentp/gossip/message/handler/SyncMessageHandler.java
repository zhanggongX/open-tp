package cn.opentp.gossip.message.handler;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.message.AckMessage;
import cn.opentp.gossip.message.SyncMessage;
import cn.opentp.gossip.message.factory.GossipMessageFactory;
import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.node.GossipNodeContext;
import cn.opentp.gossip.node.GossipNodeDigest;
import cn.opentp.gossip.node.HeartbeatState;
import cn.opentp.gossip.util.GossipJacksonUtil;
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
            List<GossipNodeDigest> gossipNodeDigests = GossipJacksonUtil.parseJsonList(data, GossipNodeDigest.class);

            List<GossipNodeDigest> needUpdateNodes = new ArrayList<>();
            Map<GossipNode, HeartbeatState> newestNodes = new HashMap<>();

            List<GossipNode> syncedNodes = new ArrayList<>();
            for (GossipNodeDigest gossipNodeDigest : gossipNodeDigests) {
                GossipNode node = new GossipNode();
                node.setCluster(cluster);
                node.setHost(gossipNodeDigest.getSocketAddress().getAddress().getHostAddress());
                node.setPort(gossipNodeDigest.getSocketAddress().getPort());
                node.setNodeId(gossipNodeDigest.getNodeId());
                syncedNodes.add(node);

                compareDigest(gossipNodeDigest, node, needUpdateNodes, newestNodes);
            }

            GossipApp gossipApp = GossipApp.instance();
            GossipNodeContext nodeContext = gossipApp.gossipNodeContext();
            // 本节点记录的集群节点信息
            Map<GossipNode, HeartbeatState> clusterNodes = nodeContext.clusterNodes();
            Set<GossipNode> clusterNodeKeys = clusterNodes.keySet();
            for (GossipNode node : clusterNodeKeys) {
                // 我有，你没有。
                if (!syncedNodes.contains(node)) {
                    newestNodes.put(node, clusterNodes.get(node));
                }
                // 如果是本节点，不管对方有没有，都返回。
                if (node.equals(gossipApp.selfNode())) {
                    newestNodes.put(node, clusterNodes.get(node));
                }
            }

            AckMessage ackMessage = new AckMessage(needUpdateNodes, newestNodes);
            ByteBuf ackByteBuf = GossipMessageFactory.factory().encodeAckMessage(ackMessage);
            if (from != null) {
                String[] host = from.split(":");
                gossipApp.networkService().send(host[0], Integer.parseInt(host[1]), ackByteBuf);
            }
        } catch (NumberFormatException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * @param gossipNodeDigest 同步过来的节点摘要
     * @param syncedNode       摘要生成的节点
     * @param needUpdateNodes  本节点需要更新的节点
     * @param newestNodes      当前节点最新的节点信息
     */
    private void compareDigest(GossipNodeDigest gossipNodeDigest, GossipNode syncedNode, List<GossipNodeDigest> needUpdateNodes, Map<GossipNode, HeartbeatState> newestNodes) {

        try {
            // 同步过来的节点的信息
            long remoteHeartbeatTime = gossipNodeDigest.getHeartbeatTime();
            long remoteVersion = gossipNodeDigest.getVersion();

            GossipNodeContext nodeContext = GossipApp.instance().gossipNodeContext();
            HeartbeatState heartbeatState = nodeContext.clusterNodes().get(syncedNode);
            if (heartbeatState != null) {
                long localHeartbeatTime = heartbeatState.getHeartbeatTime();
                long localVersion = heartbeatState.getVersion();

                if (remoteHeartbeatTime > localHeartbeatTime) {
                    needUpdateNodes.add(gossipNodeDigest);
                } else if (remoteHeartbeatTime < localHeartbeatTime) {
                    newestNodes.put(syncedNode, heartbeatState);
                } else {
                    if (remoteVersion > localVersion) {
                        needUpdateNodes.add(gossipNodeDigest);
                    } else if (remoteVersion < localVersion) {
                        newestNodes.put(syncedNode, heartbeatState);
                    }
                }
            } else {
                needUpdateNodes.add(gossipNodeDigest);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
