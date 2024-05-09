package cn.opentp.gossip.model;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author lvsq
 */
public class AckMessage implements Serializable {
    private List<GossipDigest> olders;

    private Map<GossipNode, HeartbeatState> newers;

    public AckMessage() {
    }

    public AckMessage(List<GossipDigest> olders, Map<GossipNode, HeartbeatState> newers) {
        this.olders = olders;
        this.newers = newers;
    }

    public List<GossipDigest> getOlders() {
        return olders;
    }

    public void setOlders(List<GossipDigest> olders) {
        this.olders = olders;
    }

    public Map<GossipNode, HeartbeatState> getNewers() {
        return newers;
    }

    public void setNewers(Map<GossipNode, HeartbeatState> newers) {
        this.newers = newers;
    }

    @Override
    public String toString() {
        return "AckMessage{" +
                "olders=" + olders +
                ", newers=" + newers +
                '}';
    }

}
