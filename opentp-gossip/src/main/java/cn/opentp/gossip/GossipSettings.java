package cn.opentp.gossip;

import cn.opentp.gossip.core.InMemMessageManager;
import cn.opentp.gossip.core.MessageManager;
import cn.opentp.gossip.model.SeedNode;

import java.util.ArrayList;
import java.util.List;

public class GossipSettings {
    // 同步周期
    private int gossipInterval = 1000;
    // 同步延时
    private int networkDelay = 200;
    // 服务断开阈值
    private int deleteThreshold = 3;
    // 发送节点
    private final List<SeedNode> sendNodes = new ArrayList<>();

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

    public void setSeedMembers(List<SeedNode> addNodes) {
        if (addNodes != null && !addNodes.isEmpty()) {
            for (SeedNode seed : addNodes) {
                if (!seed.eigenvalue().equalsIgnoreCase(GossipManager.instance().getSelf().eigenvalue())) {
                    if (!this.sendNodes.contains(seed)) {
                        this.sendNodes.add(seed);
                    }
                }
            }
        }
    }
}
