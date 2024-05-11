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
            log.info("接收到 node: {}, 信息: {}", node, payload);
        } else if (state == GossipStateEnum.DOWN) {
            log.info("node: {} 退出集群", node);
        } else if (state == GossipStateEnum.JOIN) {
            log.info("node: {} 加入集群。", node);
        } else if (state == GossipStateEnum.UP) {
            log.info("node: {} 活跃状态", node);
        }
    }
}
