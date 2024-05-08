package cn.opentp.gossip.core;

import cn.opentp.gossip.model.SeedMember;
import cn.opentp.gossip.net.MsgService;
import cn.opentp.gossip.net.udp.UDPMsgService;

import java.util.ArrayList;
import java.util.List;


public class GossipSettings {
    private int gossipInterval = 1000;

    private int networkDelay = 200;

    private MsgService msgService = new UDPMsgService();

    private int deleteThreshold = 3;

    private List<SeedMember> seedMembers;

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

    public List<SeedMember> getSeedMembers() {
        return seedMembers;
    }

    public void setSeedMembers(List<SeedMember> seedMembers) {
        List<SeedMember> _seedMembers = new ArrayList<>();
        if (seedMembers != null && !seedMembers.isEmpty()) {
            for (SeedMember seed : seedMembers) {
                if (!seed.eigenvalue().equalsIgnoreCase(GossipManager.getInstance().getSelf().eigenvalue())) {
                    if (!_seedMembers.contains(seed)) {
                        _seedMembers.add(seed);
                    }
                }
            }
        }
        this.seedMembers = seedMembers;
    }

    public MsgService getMsgService() {
        return msgService;
    }

    public void setMsgService(MsgService msgService) {
        this.msgService = msgService;
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
