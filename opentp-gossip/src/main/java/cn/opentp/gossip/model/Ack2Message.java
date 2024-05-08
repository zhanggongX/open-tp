package cn.opentp.gossip.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author lvsq
 */
public class Ack2Message implements Serializable {

    private Map<GossipMember, HeartbeatState> endpoints;

    public Ack2Message() {
    }

    public Ack2Message(Map<GossipMember, HeartbeatState> endpoints) {

        this.endpoints = endpoints;
    }

    @Override
    public String toString() {
        return "GossipDigestAck2Message{" +
                "endpoints=" + endpoints +
                '}';
    }

    public Map<GossipMember, HeartbeatState> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<GossipMember, HeartbeatState> endpoints) {
        this.endpoints = endpoints;
    }
}
