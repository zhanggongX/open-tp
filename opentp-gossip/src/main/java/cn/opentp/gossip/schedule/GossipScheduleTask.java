package cn.opentp.gossip.schedule;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.message.GossipMessage;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
import cn.opentp.gossip.message.holder.GossipMessageHolder;
import cn.opentp.gossip.network.NetworkService;
import cn.opentp.gossip.node.*;
import cn.opentp.gossip.util.GossipUtil;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 流言传播 Task
 */
public class GossipScheduleTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(GossipScheduleTask.class);

    @Override
    public void run() {
        GossipApp gossipApp = GossipApp.instance();
        GossipNodeContext nodeContext = gossipApp.gossipNodeContext();

        //Update local member version
        Map<GossipNode, HeartbeatState> endpointMembers = nodeContext.clusterNodes();
        HeartbeatState heartbeatState = endpointMembers.get(gossipApp.selfNode());
        long version = heartbeatState.updateVersion();
        log.trace("heartbeat version is {}", version);

        // 如果当前节点处于待加入集群状态，执行上线
        GossipNode selfNode = gossipApp.selfNode();
        if (nodeContext.discoverable(selfNode)) {
            nodeContext.up(selfNode);
        }

        try {
            // 获取当前所有节点的摘要信息，并同步出去
            List<GossipNodeDigest> nodeDigests = nodeContext.randomGossipNodeDigest();
            if (!nodeDigests.isEmpty()) {
                ByteBuf byteBuf = GossipMessageCodec.codec().encodeSyncMessage(nodeDigests);
                sendBuf(byteBuf);
            }
        } catch (UnknownHostException e) {
            log.error("获取节点摘要异常：", e);
        }

        nodeContext.checkStatus();

        log.trace("live nodes : {}", nodeContext.liveNodes());
        log.trace("dead nodes : {}", nodeContext.deadNodes());
        log.trace("cluster nodes : {}", nodeContext.clusterNodes());

        // 处理流言信息
        GossipMessageHolder messageHolder = gossipApp.gossipMessageHolder();
        if (messageHolder.isEmpty()) {
            return;
        }
        for (String messageId : messageHolder.list()) {
            GossipMessage message = messageHolder.acquire(messageId);
            int forwardCount = message.getForwardCount();
            int maxTry = GossipUtil.fanOut();
            if (forwardCount < maxTry) {
                ByteBuf byteBuf = GossipMessageCodec.codec().encodeRegularMessage(message);
                sendBuf(byteBuf);
                message.setForwardCount(forwardCount + 1);
            }
            if ((System.currentTimeMillis() - message.getCreateTime()) >= message.getEffectTime()) {
                messageHolder.remove(messageId);
            }
        }
    }

    private void sendBuf(ByteBuf byteBuf) {
        // 向活跃节点发布流言
        boolean success = sendToLiveNodes(byteBuf);
        // 向终止节点发布流言
        sendToDeadNodes(byteBuf);

        List<GossipNode> liveNodes = GossipApp.instance().gossipNodeContext().liveNodes();
        List<DiscoverNode> discoverNodes = GossipApp.instance().setting().discoverNodes();
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
        int liveSize = GossipApp.instance().gossipNodeContext().liveNodes().size();
        if (liveSize == 0) {
            return false;
        }

        boolean success = false;
        int fanOut = Math.min(liveSize, GossipUtil.fanOut());
        for (int i = 0; i < fanOut; i++) {
            int index = ThreadLocalRandom.current().nextInt(liveSize);
            success = success || sendToNode(byteBuf, GossipApp.instance().gossipNodeContext().liveNodes(), index);
        }
        return success;
    }

    /**
     * 向终止节点发送流言
     *
     * @param byteBuf 流言信息
     */
    private void sendToDeadNodes(ByteBuf byteBuf) {
        List<GossipNode> deadNodes = GossipApp.instance().gossipNodeContext().deadNodes();
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
        List<DiscoverNode> discoverNodes = GossipApp.instance().setting().discoverNodes();
        int size = discoverNodes.size();
        if (size > 0) {
            if (size == 1 && isSeedNode()) {
                return;
            }
            int index = (size == 1) ? 0 : ThreadLocalRandom.current().nextInt(size);
            if (GossipApp.instance().gossipNodeContext().liveNodes().size() == 1) {
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
            if (target.equals(GossipApp.instance().selfNode())) {
                int m_size = nodes.size();
                if (m_size == 1) {
                    return false;
                } else {
                    target = nodes.get((index + 1) % m_size);
                }
            }
            GossipApp.instance().networkService().send(target.getHost(), target.getPort(), byteBuf);
            return GossipApp.instance().setting().discoverNodes().contains(buildSeedNode(target));
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
            if (target.equals(buildSeedNode(GossipApp.instance().selfNode()))) {
                if (size == 1) {
                    return;
                } else {
                    target = discoverNodes.get((index + 1) % size);
                }
            }
            NetworkService messageService = GossipApp.instance().networkService();
            messageService.send(target.getHost(), target.getPort(), byteBuf);
        } catch (Exception e) {
            log.error("", e);
        }
    }


    private DiscoverNode buildSeedNode(GossipNode gossipNode) {
        return new DiscoverNode(gossipNode.getCluster(), gossipNode.getNodeId(), gossipNode.getHost(), gossipNode.getPort());
    }

    public boolean isSeedNode() {
        if (GossipApp.instance().getSeedNode() == null) {
            return GossipApp.instance().setting().discoverNodes().contains(buildSeedNode(GossipApp.instance().selfNode()));
        }
        return GossipApp.instance().getSeedNode();
    }
}