package cn.opentp.gossip;

import cn.opentp.gossip.event.GossipListener;
import cn.opentp.gossip.model.GossipMember;
import cn.opentp.gossip.model.SeedNode;
import cn.opentp.gossip.util.SocketAddressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Gossip 服务入口
 */
public class GossipService {

    private static final Logger log = LoggerFactory.getLogger(GossipService.class);

    private static final GossipManager gossipApp = GossipManager.instance();

    public GossipService(GossipListener listener) throws Exception {
    }

    public static void init(GossipProperties properties, GossipListener gossipListener) {
        checkParams(properties);

        List<SeedNode> seedNodes = parseConfig(properties);

        if (properties.getNodeId() == null || properties.getNodeId().isEmpty()) {
            properties.setNodeId(properties.getHost() + ":" + properties.getPort());
        }
        gossipApp.getSettings().setCluster(properties.getCluster());

        gossipApp.setLocalGossipMember(properties);

        gossipApp.setGossipListener(gossipListener);

        gossipApp.getSettings().setSeedMembers(seedNodes);
    }

    private static List<SeedNode> parseConfig(GossipProperties properties) {
        String clusterNodes = properties.getClusterNodes();
        String[] hosts = clusterNodes.split(",", -1);
        List<SeedNode> seedNodes = new ArrayList<>();

        for (String host : hosts) {
            try {
                InetSocketAddress inetSocketAddress = SocketAddressUtil.parseSocketAddress(host);
                seedNodes.add(new SeedNode(properties.getCluster(), null, inetSocketAddress.getHostName(), inetSocketAddress.getPort()));
            } catch (UnknownHostException ex) {
                log.warn("Seed provider couldn't lookup host {}", host);
            }
        }

        return seedNodes;
    }

    private static void checkParams(GossipProperties properties) {
        String f = "[%s] is required!";
        String who = null;
        if (properties.getCluster() == null || properties.getCluster().isEmpty()) {
            who = "cluster";
        } else if (properties.getHost() == null || properties.getHost().isEmpty()) {
            who = "getHost";
        } else if (properties.getPort() == null) {
            who = "port";
        } else if (properties.getClusterNodes() == null || properties.getClusterNodes().isEmpty()) {
            who = "cluster nodes";
        }
        if (who != null) {
            log.error(String.format(f, who));
            System.exit(-1);
        }
    }

    public void start() {
        if (gossipApp.isWorking()) {
            log.info("Gossip is already working");
            return;
        }

        GossipMember localGossipMember = gossipApp.getLocalGossipMember();

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
