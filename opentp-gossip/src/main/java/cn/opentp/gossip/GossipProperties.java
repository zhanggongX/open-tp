package cn.opentp.gossip;

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
    // 传播间隔，ms
    private Integer gossipInterval = 1000;
    // 传播延时，ms
    private Integer networkDelay = 200;
    // 服务断开阈值
    private Integer deleteThreshold = 3;

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

    public Integer getGossipInterval() {
        return gossipInterval;
    }

    public void setGossipInterval(Integer gossipInterval) {
        this.gossipInterval = gossipInterval;
    }

    public Integer getNetworkDelay() {
        return networkDelay;
    }

    public void setNetworkDelay(Integer networkDelay) {
        this.networkDelay = networkDelay;
    }

    public Integer getDeleteThreshold() {
        return deleteThreshold;
    }

    public void setDeleteThreshold(Integer deleteThreshold) {
        this.deleteThreshold = deleteThreshold;
    }
}
