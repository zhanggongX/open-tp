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

    private String clusterNode;
    // 发送节点
    private List<SeedNode> seedNodes;

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

    public List<SeedNode> getSeedNodes() {
        return seedNodes;
    }

    public void setSeedNodes(List<SeedNode> seedNodes) {
        this.seedNodes = seedNodes;
    }

    public String getClusterNode() {
        return clusterNode;
    }

    public void setClusterNode(String clusterNode) {
        this.clusterNode = clusterNode;
    }
}
