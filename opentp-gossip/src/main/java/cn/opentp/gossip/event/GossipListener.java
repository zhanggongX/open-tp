package cn.opentp.gossip.event;


import cn.opentp.gossip.model.GossipMember;
import cn.opentp.gossip.enums.GossipStateEnum;

public interface GossipListener {

    void gossipEvent(GossipMember member, GossipStateEnum state, Object payload);
}
