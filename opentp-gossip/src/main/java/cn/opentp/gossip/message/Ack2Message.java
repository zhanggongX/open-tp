package cn.opentp.gossip.message;

import cn.opentp.gossip.model.GossipNode;
import cn.opentp.gossip.model.HeartbeatState;

import java.io.Serializable;
import java.util.Map;


public class Ack2Message implements Serializable {

    private Map<GossipNode, HeartbeatState> endpoints;

    public Ack2Message() {
    }

    public Ack2Message(Map<GossipNode, HeartbeatState> endpoints) {

        this.endpoints = endpoints;
    }

    @Override
    public String toString() {
        return "GossipDigestAck2Message{" + "endpoints=" + endpoints + '}';
    }

    public Map<GossipNode, HeartbeatState> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<GossipNode, HeartbeatState> endpoints) {
        this.endpoints = endpoints;
    }
}
