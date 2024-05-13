package cn.opentp.gossip.event;

import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.enums.GossipStateEnum;

public interface GossipListener {

    void gossipEvent(GossipNode gossipNode, GossipStateEnum gossipState, Object payload);
}
