package cn.opentp.gossip.event;

import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.node.GossipNode;

/**
 * 事件监听器上线文
 */
public class GossipListenerContext {

    private GossipListener listener;

    public void fireGossipEvent(GossipNode member, GossipStateEnum state) {
        if (listener == null) return;
        fireGossipEvent(member, state, null);
    }

    public void fireGossipEvent(GossipNode member, GossipStateEnum state, Object payload) {
        if (listener == null) return;

        if (state == GossipStateEnum.RECEIVE) {
            // todo
            new Thread(() -> listener.gossipEvent(member, state, payload)).start();
        } else {
            listener.gossipEvent(member, state, payload);
        }
    }

    public void setGossipListener(GossipListener gossipListener) {
        this.listener = gossipListener;
    }
}
