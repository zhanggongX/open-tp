package cn.opentp.gossip.event;


import cn.opentp.gossip.model.GossipNode;
import cn.opentp.gossip.enums.GossipStateEnum;

public interface GossipListener {

    void gossipEvent(GossipNode member, GossipStateEnum state, Object payload);
}
