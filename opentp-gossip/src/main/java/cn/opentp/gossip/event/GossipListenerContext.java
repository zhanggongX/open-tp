package cn.opentp.gossip.event;

import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.node.GossipNode;

/**
 * 事件监听器环境
 */
public class GossipListenerContext {

    private GossipListener listener;

    public void fireGossipEvent(GossipNode member, GossipStateEnum state) {
        fireGossipEvent(member, state, null);
    }

    public void fireGossipEvent(GossipNode member, GossipStateEnum state, Object payload) {
        if (listener == null) return;
        listener.gossipEvent(member, state, payload);
    }

    public void setGossipListener(GossipListener gossipListener) {
        this.listener = gossipListener;
    }
}
