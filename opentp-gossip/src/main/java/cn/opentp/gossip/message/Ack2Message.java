package cn.opentp.gossip.message;

import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.node.HeartbeatState;

import java.io.Serializable;
import java.util.Map;

/**
 * 节点信息同步应答的应答消息
 */
public class Ack2Message implements Serializable {

    private Map<GossipNode, HeartbeatState> clusterNodes;

    public Ack2Message() {
    }

    public Ack2Message(Map<GossipNode, HeartbeatState> clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public Map<GossipNode, HeartbeatState> getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(Map<GossipNode, HeartbeatState> clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    @Override
    public String toString() {
        return "Ack2Message{" +
                "clusterNodes=" + clusterNodes +
                '}';
    }
}
