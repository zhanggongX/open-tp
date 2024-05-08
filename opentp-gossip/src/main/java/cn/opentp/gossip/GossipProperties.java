package cn.opentp.gossip;

import java.net.InetSocketAddress;

public class GossipProperties {

    // 集群名
    private String clusterName = "Opentp Cluster";
    // 失败阈值
    private Integer failThreshold = 8;
    // 监听地址
    private String listenAddress;
    // 集群节点
    private String clusterNodes;
    // 监听端口
    private InetSocketAddress inetSocketAddress;

    public GossipProperties() {
    }

    public GossipProperties(String listenAddress, String clusterNodes) {
        this.listenAddress = listenAddress;
        this.clusterNodes = clusterNodes;
    }

    public GossipProperties(String clusterName, Integer failThreshold, String listenAddress, String clusterNodes) {
        this.clusterName = clusterName;
        this.failThreshold = failThreshold;
        this.listenAddress = listenAddress;
        this.clusterNodes = clusterNodes;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Integer getFailThreshold() {
        return failThreshold;
    }

    public void setFailThreshold(Integer failThreshold) {
        this.failThreshold = failThreshold;
    }

    public String getListenAddress() {
        return listenAddress;
    }

    public void setListenAddress(String listenAddress) {
        this.listenAddress = listenAddress;
    }

    public String getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }
}