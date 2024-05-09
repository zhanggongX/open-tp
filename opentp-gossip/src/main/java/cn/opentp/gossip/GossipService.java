package cn.opentp.gossip;

import cn.opentp.gossip.event.GossipListener;
import cn.opentp.gossip.model.SeedNode;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

/**
 * Gossip 服务入口
 */
public class GossipService {

    private static final Logger log = LoggerFactory.getLogger(GossipService.class);

    private final GossipManager gossipManager = GossipManager.instance();

    public GossipService(GossipListener listener) throws Exception {
    }

    public void start() {
        if (gossipManager.isWorking()) {
            log.info("Gossip is already working");
            return;
        }
        GossipManager.instance().start();
    }

    public void shutdown() {
        if (gossipManager.isWorking()) {
            GossipManager.instance().shutdown();
        }
    }
}
