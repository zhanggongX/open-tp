package cn.opentp.gossip.model;

import cn.opentp.gossip.enums.GossipStateEnum;

import java.io.Serializable;
import java.util.Objects;

public class GossipNode implements Serializable {

    private String cluster;
    private String host;
    private Integer port;
    private String nodeId;
    private GossipStateEnum state;

    public GossipNode() {
    }

    public GossipNode(String cluster, String host, Integer port, String nodeId, GossipStateEnum state) {
        this.cluster = cluster;
        this.host = host;
        this.port = port;
        this.nodeId = nodeId;
        this.state = state;
    }

    public String socketAddress() {
        return host + ":" + port;
    }

    public String eigenvalue() {
        return cluster + ":" + socketAddress();
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public GossipStateEnum getState() {
        return state;
    }

    public void setState(GossipStateEnum state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GossipNode that = (GossipNode) o;
        return Objects.equals(cluster, that.cluster) && Objects.equals(host, that.host) && Objects.equals(port, that.port) && Objects.equals(nodeId, that.nodeId) && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cluster, host, port, nodeId, state);
    }

    @Override
    public String toString() {
        return "GossipNode{" + "cluster='" + cluster + '\'' + ", host='" + host + '\'' + ", port=" + port + ", nodeId='" + nodeId + '\'' + ", state=" + state + '}';
    }
}
