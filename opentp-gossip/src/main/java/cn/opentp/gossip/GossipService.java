package cn.opentp.gossip;

import cn.opentp.gossip.event.GossipListener;
import cn.opentp.gossip.model.GossipMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gossip 服务入口
 */
public class GossipService {

    private static final Logger log = LoggerFactory.getLogger(GossipService.class);

    private static final GossipManager gossipApp = GossipManager.instance();

    public static void init(GossipProperties properties) {
        GossipSettings.parseConfig(properties);
    }

    public static void init(GossipProperties properties, GossipListener gossipListener) {
        init(properties);
        gossipApp.setGossipListener(gossipListener);
    }

    public static void start() {
        if (gossipApp.isWorking()) {
            log.info("Gossip is already working");
            return;
        }

        GossipMember localGossipMember = gossipApp.selfNode();

        log.info(String.format("Starting jgossip! cluster[%s] ip[%s] port[%d] id[%s]", localGossipMember.getCluster(), localGossipMember.getIpAddress(), localGossipMember.getPort(), localGossipMember.getId()
        ));
        gossipApp.setWorking();
        gossipApp.startListen();
        gossipApp.startTask();
    }

    public void shutdown() {
        if (gossipApp.isWorking()) {
            GossipManager.instance().shutdown();
        }
    }
}
