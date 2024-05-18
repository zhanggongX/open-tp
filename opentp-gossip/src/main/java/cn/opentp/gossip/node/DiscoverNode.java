package cn.opentp.gossip.node;

import java.io.Serializable;
import java.util.Objects;

/**
 * 通过属性配置的发现节点
 */
public class DiscoverNode implements Serializable {

    private String nodeId;
    private String cluster;
    private String host;
    private Integer port;

    public DiscoverNode() {
    }

    public DiscoverNode(String cluster, String nodeId, String host, Integer port) {
        this.cluster = cluster;
        this.nodeId = nodeId;
        this.host = host;
        this.port = port;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
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

    public String eigenvalue() {
        return getCluster().concat(":").concat(getHost()).concat(":").concat(getPort().toString());
    }

    /**
     * 不比较 nodeId
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscoverNode seedNode = (DiscoverNode) o;
        return Objects.equals(cluster, seedNode.cluster) && Objects.equals(host, seedNode.host) && Objects.equals(port, seedNode.port);
    }

    /**
     * 不计算 nodeId
     */
    @Override
    public int hashCode() {
        return Objects.hash(cluster, host, port);
    }

    @Override
    public String toString() {
        return "DiscoverNode{" + "nodeId='" + nodeId + '\'' + ", cluster='" + cluster + '\'' + ", host='" + host + '\'' + ", port=" + port + '}';
    }
}
