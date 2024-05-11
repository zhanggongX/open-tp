package cn.opentp.gossip;

import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.model.GossipNode;
import cn.opentp.gossip.model.HeartbeatState;
import cn.opentp.gossip.model.SeedNode;
import cn.opentp.gossip.util.SocketAddressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局配置
 */
public class GossipSettings {

    private static final Logger log = LoggerFactory.getLogger(GossipSettings.class);

    // 集群名 must
    private String cluster = "Opentp";
    // 主机地址 must
    private String host;
    // 端口 must
    private Integer port;
    // 节点名
    private String nodeId;
    // 原配置集群节点 must
    private String originalClusterNodes;

    // 传播间隔，ms
    private int gossipInterval = 1000;
    // 传播延时，ms
    private int networkDelay = 200;
    // 服务断开阈值
    private int deleteThreshold = 3;

    // 本地节点
    private GossipNode localNode;
    // 集群节点
    private final List<SeedNode> sendNodes = new ArrayList<>();

    public static void parseConfig(GossipProperties properties) {
        // 校验必要配置
        checkParams(properties);

        // 默认 NODE ID
        if (properties.getNodeId() == null || properties.getNodeId().isEmpty()) {
            properties.setNodeId(properties.getHost() + ":" + properties.getPort());
        }

        // 解析 clusterNodes
        GossipApp gossipApp = GossipApp.instance();
        GossipSettings gossipSettings = gossipApp.setting();
        String clusterNodes = properties.getClusterNodes();
        String[] hosts = clusterNodes.split(",", -1);

        for (String host : hosts) {
            try {
                InetSocketAddress inetSocketAddress = SocketAddressUtil.parseSocketAddress(host);
                gossipSettings.getSendNodes().add(new SeedNode(properties.getCluster(), null, inetSocketAddress.getHostName(), inetSocketAddress.getPort()));
            } catch (UnknownHostException ex) {
                log.warn("Seed provider couldn't lookup host {}", host);
            }
        }

        // 其他参数
        gossipSettings.setCluster(properties.getCluster());
        gossipSettings.setHost(properties.getHost());
        gossipSettings.setPort(properties.getPort());
        gossipSettings.setNodeId(properties.getNodeId());
        gossipSettings.setOriginalClusterNodes(properties.getClusterNodes());
        int gossipInterval = properties.getGossipInterval() == null ? gossipSettings.getGossipInterval() : properties.getGossipInterval();
        gossipSettings.setGossipInterval(gossipInterval);
        int networkDelay = properties.getNetworkDelay() == null ? gossipSettings.getNetworkDelay() : properties.getNetworkDelay();
        gossipSettings.setNetworkDelay(networkDelay);
        int deleteThreshold = properties.getDeleteThreshold() == null ? gossipSettings.getDeleteThreshold() : properties.getDeleteThreshold();
        gossipSettings.setDeleteThreshold(deleteThreshold);

        // 本地节点
        GossipNode gossipNode = new GossipNode();
        gossipNode.setCluster(properties.getCluster());
        gossipNode.setHost(properties.getHost());
        gossipNode.setPort(properties.getPort());
        gossipNode.setNodeId(properties.getNodeId());
        gossipNode.setState(GossipStateEnum.JOIN);
        gossipSettings.setLocalNode(gossipNode);

        gossipApp.endpointNodeCache().put(gossipNode, new HeartbeatState());
    }

    public GossipNode getLocalNode() {
        return localNode;
    }

    public void setLocalNode(GossipNode localNode) {
        this.localNode = localNode;
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

    public String getOriginalClusterNodes() {
        return originalClusterNodes;
    }

    public void setOriginalClusterNodes(String originalClusterNodes) {
        this.originalClusterNodes = originalClusterNodes;
    }

    public int getGossipInterval() {
        return gossipInterval;
    }

    public void setGossipInterval(int gossipInterval) {
        this.gossipInterval = gossipInterval;
    }

    public int getNetworkDelay() {
        return networkDelay;
    }

    public void setNetworkDelay(int networkDelay) {
        this.networkDelay = networkDelay;
    }

    public int getDeleteThreshold() {
        return deleteThreshold;
    }

    public void setDeleteThreshold(int deleteThreshold) {
        this.deleteThreshold = deleteThreshold;
    }

    public List<SeedNode> getSendNodes() {
        return sendNodes;
    }

    public static void checkParams(GossipProperties properties) {
        String f = "[%s] is required!";
        String who = null;
        if (properties.getCluster() == null || properties.getCluster().isEmpty()) {
            who = "cluster";
        } else if (properties.getHost() == null || properties.getHost().isEmpty()) {
            who = "getHost";
        } else if (properties.getPort() == null) {
            who = "port";
        } else if (properties.getClusterNodes() == null || properties.getClusterNodes().isEmpty()) {
            who = "cluster nodes";
        }
        if (who != null) {
            log.error(String.format(f, who));
            System.exit(-1);
        }
    }
}
