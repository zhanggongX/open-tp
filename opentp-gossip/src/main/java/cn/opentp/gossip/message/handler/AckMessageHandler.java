package cn.opentp.gossip.message.handler;

import cn.opentp.core.util.JacksonUtil;
import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.message.Ack2Message;
import cn.opentp.gossip.message.AckMessage;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.node.GossipNodeContext;
import cn.opentp.gossip.node.GossipNodeDigest;
import cn.opentp.gossip.node.HeartbeatState;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AckMessageHandler implements MessageHandler {

    private final static Logger log = LoggerFactory.getLogger(AckMessageHandler.class);

    @Override
    public void handle(String cluster, byte[] data, String from) {
        GossipNodeContext nodeContext = GossipApp.instance().gossipNodeContext();

        AckMessage ackMessage = GossipMessageCodec.codec().decodeMessage(data, AckMessage.class);
        log.trace("ack message: {}", JacksonUtil.toJSONString(ackMessage));

        List<GossipNodeDigest> remoteNeedUpdateNodes = ackMessage.getNeedUpdateNodes();
        Map<GossipNode, HeartbeatState> newestNodes = ackMessage.getNewestNodes();

        if (!newestNodes.isEmpty()) {
            nodeContext.updateLocalClusterNodes(newestNodes);
        }

        Map<GossipNode, HeartbeatState> deltaGossipNodes = new HashMap<>();
        if (remoteNeedUpdateNodes != null) {
            for (GossipNodeDigest needUpdateNodeDigest : remoteNeedUpdateNodes) {
                GossipNode remoteNeedUpdateNode = restoreGossipNode(needUpdateNodeDigest);
                HeartbeatState heartbeatState = nodeContext.clusterNodes().get(remoteNeedUpdateNode);
                if (heartbeatState != null) {
                    deltaGossipNodes.put(remoteNeedUpdateNode, heartbeatState);
                }
            }
        }

        // 同步回去对方需要更新的节点信息，完成一次信息交换。
        if (!deltaGossipNodes.isEmpty()) {
            Ack2Message ack2Message = new Ack2Message(deltaGossipNodes);
            ByteBuf byteBuf = GossipMessageCodec.codec().encodeAck2Message(ack2Message);
            if (from != null) {
                String[] host = from.split(":");
                GossipApp.instance().networkService().send(host[0], Integer.valueOf(host[1]), byteBuf);
            }
            byteBuf.release();
        }
    }

    /**
     * 根据签名还原节点信息
     * 注意该方法不具有普适性，只是这里是响应通信的处理，说明本集群一定有该节点。
     *
     * @param nodeDigest 节点签名
     * @return 节点信息
     */
    private GossipNode restoreGossipNode(GossipNodeDigest nodeDigest) {
        GossipNode node = new GossipNode();
        node.setPort(nodeDigest.getPort());
        node.setHost(nodeDigest.getHost());
        node.setCluster(GossipApp.instance().setting().getCluster());

        GossipNodeContext gossipNodeContext = GossipApp.instance().gossipNodeContext();
        Set<GossipNode> clusterNodeKeys = gossipNodeContext.clusterNodes().keySet();
        for (GossipNode clusterNode : clusterNodeKeys) {
            if (clusterNode.equals(node)) {
                node.setNodeId(clusterNode.getNodeId());
                node.setState(clusterNode.getState());
                break;
            }
        }
        return node;
    }
}
