package cn.opentp.gossip;

import cn.opentp.gossip.model.SeedNode;

import java.util.List;

public class GossipProperties {
    // 集群名
    private String cluster;
    // 主机地址
    private String host;
    // 端口
    private Integer port;
    // 节点名
    private String nodeId;
    // 集群节点
    private String clusterNodes;

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

    public String getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }
}
