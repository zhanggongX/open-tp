package cn.opentp.gossip.event;


import cn.opentp.gossip.model.GossipMember;
import cn.opentp.gossip.model.GossipState;

public interface GossipListener {

    void gossipEvent(GossipMember member, GossipState state, Object payload);
}
