package cn.opentp.gossip.core;

import cn.opentp.gossip.event.GossipListener;
import cn.opentp.gossip.model.SeedMember;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

public class GossipService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GossipService.class);

    public GossipService(String cluster, String ipAddress, Integer port, String id, List<SeedMember> seedMembers, GossipSettings settings, GossipListener listener) throws Exception {
        checkParams(cluster, ipAddress, port, seedMembers);
        if (StringUtil.isNullOrEmpty(id)) {
            id = ipAddress.concat(":").concat(String.valueOf(port));
        }
        GossipManager.getInstance().init(cluster, ipAddress, port, id, seedMembers, settings, listener);
    }

    public GossipManager getGossipManager() {
        return GossipManager.getInstance();
    }

    public void start() {
        if (getGossipManager().isWorking()) {
            LOGGER.info("jgossip is already working");
            return;
        }
        GossipManager.getInstance().start();
    }

    public void shutdown() {
        if (getGossipManager().isWorking()) {
            GossipManager.getInstance().shutdown();
        }
    }

    private void checkParams(String cluster, String ipAddress, Integer port, List<SeedMember> seedMembers) throws Exception {
        String f = "[%s] is required!";
        String who = null;
        if (StringUtil.isNullOrEmpty(cluster)) {
            who = "cluster";
        } else if (StringUtil.isNullOrEmpty(ipAddress)) {
            who = "ip";
        } else if (StringUtil.isNullOrEmpty(String.valueOf(port))) {
            who = "port";
        } else if (seedMembers == null || seedMembers.isEmpty()) {
            who = "seed member";
        }
        if (who != null) {
            throw new IllegalArgumentException(String.format(f, who));
        }
    }
}
