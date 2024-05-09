package cn.opentp.gossip;

import cn.opentp.gossip.core.InMemMessageManager;
import cn.opentp.gossip.core.MessageManager;
import cn.opentp.gossip.model.SeedNode;

import java.util.ArrayList;
import java.util.List;

public class GossipSettings {

    private int gossipInterval = 1000;

    private int networkDelay = 200;

    private int deleteThreshold = 3;

    private List<SeedNode> seedMembers;

    private MessageManager messageManager = new InMemMessageManager();

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

    public List<SeedNode> getSeedMembers() {
        return seedMembers;
    }

    public void setSeedMembers(List<SeedNode> seedMembers) {
        List<SeedNode> _seedMembers = new ArrayList<>();
        if (seedMembers != null && !seedMembers.isEmpty()) {
            for (SeedNode seed : seedMembers) {
                if (!seed.eigenvalue().equalsIgnoreCase(GossipManager.instance().getSelf().eigenvalue())) {
                    if (!_seedMembers.contains(seed)) {
                        _seedMembers.add(seed);
                    }
                }
            }
        }
        this.seedMembers = seedMembers;
    }

    public int getDeleteThreshold() {
        return deleteThreshold;
    }

    public void setDeleteThreshold(int deleteThreshold) {
        this.deleteThreshold = deleteThreshold;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public void setMessageManager(MessageManager messageManager) {
        this.messageManager = messageManager;
    }
}
