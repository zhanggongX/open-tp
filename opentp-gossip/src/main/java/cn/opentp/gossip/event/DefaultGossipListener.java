package cn.opentp.gossip.event;

import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.model.GossipNode;

public class DefaultGossipListener implements GossipListener {

    @Override
    public void gossipEvent(GossipNode member, GossipStateEnum state, Object payload) {
        if (state == GossipStateEnum.RECEIVE) {
            System.out.println("member:" + member + "  state: " + state + " payload: " + payload);
        }
        if (state == GossipStateEnum.DOWN) {
            System.out.println("[[[[[[[[[member:" + member + "  was down!!! ]]]]]]]]]");
        }
    }
}
