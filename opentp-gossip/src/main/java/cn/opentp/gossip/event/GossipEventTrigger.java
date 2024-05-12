package cn.opentp.gossip.event;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.node.GossipNode;

/**
 * 事件触发器
 */
public class GossipEventTrigger {

    public static void fireGossipEvent(GossipNode member, GossipStateEnum state) {
        fireGossipEvent(member, state, null);
    }

    public static void fireGossipEvent(GossipNode member, GossipStateEnum state, Object payload) {
        if (GossipApp.instance().listener() != null) {
            if (state == GossipStateEnum.RECEIVE) {
                // todo
                new Thread(() -> GossipApp.instance().listener().gossipEvent(member, state, payload)).start();
            } else {
                GossipApp.instance().listener().gossipEvent(member, state, payload);
            }
        }
    }
}
