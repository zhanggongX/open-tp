package cn.opentp.gossip.schedule;

import cn.opentp.gossip.GossipEnvironment;
import cn.opentp.gossip.GossipSettings;
import cn.opentp.gossip.network.NetworkService;
import cn.opentp.gossip.node.DiscoverNode;
import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.util.GossipUtil;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractGossipTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(AbstractGossipTask.class);

    protected void sendBuf(ByteBuf byteBuf) {
        // 向活跃节点发布流言
        boolean success = sendToLiveNodes(byteBuf);
        // 向终止节点发布流言
        sendToDownedNodes(byteBuf);

        List<GossipNode> liveNodes = GossipEnvironment.instance().gossipNodeContext().liveNodes();
        List<DiscoverNode> discoverNodes = GossipEnvironment.instance().setting().discoverNodes();
        if (!success || liveNodes.size() <= discoverNodes.size()) {
            // 向配置的 discovery 节点发送流言信息
            sendToDiscoveryNode(byteBuf);
        }
    }

    /**
     * 向活跃节点发布流言。
     *
     * @param byteBuf 流言信息
     * @return 发送成功
     */
    private boolean sendToLiveNodes(ByteBuf byteBuf) {
        int liveSize = GossipEnvironment.instance().gossipNodeContext().liveNodes().size();
        if (liveSize == 0) {
            return false;
        }

        boolean success = false;
        int fanOut = Math.min(liveSize, GossipUtil.fanOut());
        for (int i = 0; i < fanOut; i++) {
            int index = ThreadLocalRandom.current().nextInt(liveSize);
            success = success || sendToNode(byteBuf, GossipEnvironment.instance().gossipNodeContext().liveNodes(), index);
        }
        return success;
    }

    /**
     * 向终止节点发送流言
     *
     * @param byteBuf 流言信息
     */
    private void sendToDownedNodes(ByteBuf byteBuf) {
        List<GossipNode> deadNodes = GossipEnvironment.instance().gossipNodeContext().downedNodes();
        int deadSize = deadNodes.size();
        if (deadSize == 0) {
            return;
        }
        int index = (deadSize == 1) ? 0 : ThreadLocalRandom.current().nextInt(deadSize);
        sendToNode(byteBuf, deadNodes, index);
    }

    /**
     * 向配置的发现节点发送流言
     *
     * @param byteBuf 流言信息
     */
    private void sendToDiscoveryNode(ByteBuf byteBuf) {
        GossipSettings setting = GossipEnvironment.instance().setting();
        List<DiscoverNode> discoverNodes = setting.discoverNodes();
        int size = discoverNodes.size();
        if (size > 0) {
            if (size == 1 && setting.isDiscoverNode()) {
                return;
            }
            int index = (size == 1) ? 0 : ThreadLocalRandom.current().nextInt(size);
            if (GossipEnvironment.instance().gossipNodeContext().liveNodes().size() == 1) {
                sendDiscoveryNode(byteBuf, discoverNodes, index);
            } else {
                double prob = size / (double) discoverNodes.size();
                if (ThreadLocalRandom.current().nextDouble() < prob) {
                    sendDiscoveryNode(byteBuf, discoverNodes, index);
                }
            }
        }
    }

    /**
     * 向集群节点发送流言
     *
     * @param byteBuf 流言信息
     * @param nodes   集群节点
     * @param index   集群节点索引
     * @return 状态
     */
    private boolean sendToNode(ByteBuf byteBuf, List<GossipNode> nodes, int index) {
        if (byteBuf == null || index < 0) {
            return false;
        }

        try {
            GossipNode target = nodes.get(index);
            if (target.equals(GossipEnvironment.instance().selfNode())) {
                int m_size = nodes.size();
                if (m_size == 1) {
                    return false;
                } else {
                    target = nodes.get((index + 1) % m_size);
                }
            }
            GossipEnvironment.instance().networkService().send(target.getHost(), target.getPort(), byteBuf);
            return GossipEnvironment.instance().setting().discoverNodes().contains(buildDiscoverNode(target));
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 向配置的发现节点发送流言
     *
     * @param byteBuf       流言信息
     * @param discoverNodes 配置的发现节点
     * @param index         发现节点索引
     */
    private void sendDiscoveryNode(ByteBuf byteBuf, List<DiscoverNode> discoverNodes, int index) {
        if (byteBuf == null && index < 0) {
            return;
        }

        try {
            DiscoverNode target = discoverNodes.get(index);
            int size = discoverNodes.size();
            if (target.equals(buildDiscoverNode(GossipEnvironment.instance().selfNode()))) {
                if (size == 1) {
                    return;
                } else {
                    target = discoverNodes.get((index + 1) % size);
                }
            }
            NetworkService messageService = GossipEnvironment.instance().networkService();
            messageService.send(target.getHost(), target.getPort(), byteBuf);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private DiscoverNode buildDiscoverNode(GossipNode gossipNode) {
        return new DiscoverNode(gossipNode.getCluster(), gossipNode.getNodeId(), gossipNode.getHost(), gossipNode.getPort());
    }
}
