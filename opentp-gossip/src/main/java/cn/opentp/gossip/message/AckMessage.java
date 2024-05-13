package cn.opentp.gossip.message;

import cn.opentp.gossip.node.GossipNodeDigest;
import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.node.HeartbeatState;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 节点信息同步应答消息
 */
public class AckMessage implements Serializable {

    private List<GossipNodeDigest> needUpdateNodes;
    private Map<GossipNode, HeartbeatState> newestNodes;

    public AckMessage() {
    }

    public AckMessage(List<GossipNodeDigest> needUpdateNodes, Map<GossipNode, HeartbeatState> newestNodes) {
        this.needUpdateNodes = needUpdateNodes;
        this.newestNodes = newestNodes;
    }

    public List<GossipNodeDigest> getNeedUpdateNodes() {
        return needUpdateNodes;
    }

    public void setNeedUpdateNodes(List<GossipNodeDigest> needUpdateNodes) {
        this.needUpdateNodes = needUpdateNodes;
    }

    public Map<GossipNode, HeartbeatState> getNewestNodes() {
        return newestNodes;
    }

    public void setNewestNodes(Map<GossipNode, HeartbeatState> newestNodes) {
        this.newestNodes = newestNodes;
    }

    @Override
    public String toString() {
        return "AckMessage{" + "needUpdateNodes=" + needUpdateNodes + ", newestNodes=" + newestNodes + '}';
    }
}
