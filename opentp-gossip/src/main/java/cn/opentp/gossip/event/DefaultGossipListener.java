package cn.opentp.gossip.event;

import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.model.GossipNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultGossipListener implements GossipListener {

    private static final Logger log = LoggerFactory.getLogger(DefaultGossipListener.class);

    @Override
    public void gossipEvent(GossipNode node, GossipStateEnum state, Object payload) {
        if (state == GossipStateEnum.RECEIVE) {
            log.info("node: {}, state: {}, payload:{}", node, state, payload);
        }
        if (state == GossipStateEnum.DOWN) {
            log.info("node: {} was downed", node);
        }
    }
}
