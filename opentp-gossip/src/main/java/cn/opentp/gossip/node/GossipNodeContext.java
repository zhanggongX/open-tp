package cn.opentp.gossip.node;

import cn.opentp.gossip.GossipEnvironment;
import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
import cn.opentp.gossip.util.GossipUtil;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Gossip 集群节点环境
 */
public class GossipNodeContext {

    private static final Logger log = LoggerFactory.getLogger(GossipNodeContext.class);

    // 所有节点
    private final Map<GossipNode, HeartbeatState> clusterNodes = new ConcurrentHashMap<>();
    // 活节点
    private final List<GossipNode> liveNodes = new CopyOnWriteArrayList<>();
    // 死节点
    private final List<GossipNode> downedNodes = new CopyOnWriteArrayList<>();
    // 候选节点，判定中的节点
    private final Map<GossipNode, DowningNodeState> downingNodes = new ConcurrentHashMap<>();

    public Map<GossipNode, HeartbeatState> clusterNodes() {
        return clusterNodes;
    }

    public List<GossipNode> liveNodes() {
        return liveNodes;
    }

    public List<GossipNode> downedNodes() {
        return downedNodes;
    }

    public Map<GossipNode, DowningNodeState> downingNodes() {
        return downingNodes;
    }

    /**
     * 节点上线
     *
     * @param node 上线节点
     */
    public void up(GossipNode node) {
        ReentrantReadWriteLock.WriteLock writeLock = GossipEnvironment.instance().lock().writeLock();
        try {
            writeLock.lock();
            node.setState(GossipStateEnum.UP);
            // 非原子操作
            if (!liveNodes().contains(node)) {
                liveNodes().add(node);
            }
            // 候选节点移除
            downingNodes().remove(node);
            // 终止节点移除
            downedNodes().remove(node);

            if (!node.equals(GossipEnvironment.instance().selfNode())) {
                // 触发上线事件
                GossipEnvironment.instance().gossipListenerContext().fireGossipEvent(node, GossipStateEnum.UP);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            writeLock.unlock();
        }
    }

    public void downing(GossipNode node, HeartbeatState state) {
        try {
            if (downingNodes().containsKey(node)) {
                DowningNodeState downingNodeState = downingNodes().get(node);
                if (state.getHeartbeatTime() == downingNodeState.getHeartbeatTime()) {
                    // 没有心跳到来，downing count ++;
                    downingNodeState.updateCount();
                } else if (state.getHeartbeatTime() > downingNodeState.getHeartbeatTime()) {
                    // 有更新，移出即将下线节点
                    downingNodes().remove(node);
                }
            } else {
                // 新的，放入候选节点
                downingNodes().put(node, new DowningNodeState(state.getHeartbeatTime()));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 节点下线
     *
     * @param node 下线节点
     */
    public void down(GossipNode node) {
        ReentrantReadWriteLock.WriteLock writeLock = GossipEnvironment.instance().lock().writeLock();
        try {
            writeLock.lock();
            node.setState(GossipStateEnum.DOWN);
            // 活动节点移除
            liveNodes().remove(node);
            // 加入终止节点
            if (!downedNodes().contains(node)) {
                // 非原子操作
                downedNodes().add(node);
            }
            // 触发下线事件
            GossipEnvironment.instance().gossipListenerContext().fireGossipEvent(node, GossipStateEnum.DOWN);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 本节点下线，发送下线信息
     */
    public void selfNodeShutdown() {
        GossipEnvironment environment = GossipEnvironment.instance();
        GossipNode selfNode = environment.selfNode();
        ByteBuf byteBuf = GossipMessageCodec.codec().encodeShutdownMessage(selfNode);
        for (GossipNode node : liveNodes()) {
            try {
                if (!node.equals(selfNode)) {
                    environment.networkService().send(node.getHost(), node.getPort(), byteBuf);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        byteBuf.release();
    }

    /**
     * 随机当前所有节点的摘要信息
     *
     * @throws UnknownHostException 节点网络地址异常
     */
    public List<GossipNodeDigest> randomGossipNodeDigest() throws UnknownHostException {
        List<GossipNodeDigest> nodeDigests = new ArrayList<>();

        List<GossipNode> nodes = new ArrayList<>(clusterNodes().keySet());
        Collections.shuffle(nodes, ThreadLocalRandom.current());

        for (GossipNode node : nodes) {
            HeartbeatState heartbeatState = clusterNodes().get(node);
            long time = 0;
            long version = 0;
            if (heartbeatState != null) {
                time = heartbeatState.getHeartbeatTime();
                version = heartbeatState.getVersion();
            }
            nodeDigests.add(new GossipNodeDigest(node, time, version));
        }
        return nodeDigests;
    }

    /**
     * 检查本节点集群状态
     */
    public void checkStatus() {
        try {
            GossipNode localNode = GossipEnvironment.instance().selfNode();
            Map<GossipNode, HeartbeatState> clusteredNodes = clusterNodes();

            for (Map.Entry<GossipNode, HeartbeatState> nodeEntry : clusteredNodes.entrySet()) {
                GossipNode node = nodeEntry.getKey();
                HeartbeatState heartbeatState = nodeEntry.getValue();

                if (node.equals(localNode)) {
                    continue;
                }

                long now = System.currentTimeMillis();
                long duration = now - heartbeatState.getHeartbeatTime();
                long convictedTime = GossipUtil.convictedTime();
                log.debug("check: {}, state: {}, duration: {}, convictedTime: {}", node, heartbeatState, duration, convictedTime);
                if (duration > convictedTime && (isAlive(node) || GossipEnvironment.instance().gossipNodeContext().liveNodes().contains(node))) {
                    // 长时间没有心跳，准备下线
                    downing(node, heartbeatState);
                }
                if (duration <= convictedTime && (discoverable(node) || GossipEnvironment.instance().gossipNodeContext().downedNodes().contains(node))) {
                    // 重新上线
                    up(node);
                }
            }
            // 检查即将下线节点
            checkDowning();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 更新本节点集群信息
     *
     * @param remoteClusterNodes 远程集群信息
     */
    public void updateLocalClusterNodes(Map<GossipNode, HeartbeatState> remoteClusterNodes) {

        GossipNode selfNode = GossipEnvironment.instance().selfNode();
        Map<GossipNode, HeartbeatState> clusterNodes = GossipEnvironment.instance().gossipNodeContext().clusterNodes();

        for (Map.Entry<GossipNode, HeartbeatState> nodeEntry : remoteClusterNodes.entrySet()) {
            GossipNode remoteNode = nodeEntry.getKey();
            HeartbeatState remoteNodeHb = nodeEntry.getValue();

            if (selfNode.equals(remoteNode)) {
                continue;
            }

            try {
                HeartbeatState localState = clusterNodes.get(remoteNode);
                if (localState != null) {
                    int compareRes = localState.compareTo(remoteNodeHb);
                    if (compareRes < 0) {
                        updateClusterNodes(remoteNode, remoteNodeHb);
                    }
                } else {
                    // 如果本地没有，直接更新
                    updateClusterNodes(remoteNode, remoteNodeHb);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * 根据远程节点信息，更新集群节点信息。
     *
     * @param remoteNode  远程节点信息
     * @param remoteState 远程节点状态
     */
    private void updateClusterNodes(GossipNode remoteNode, HeartbeatState remoteState) {
        GossipNodeContext nodeContext = GossipEnvironment.instance().gossipNodeContext();
        if (remoteNode.getState() == GossipStateEnum.UP) {
            nodeContext.up(remoteNode);
        }
        if (remoteNode.getState() == GossipStateEnum.DOWN) {
            nodeContext.down(remoteNode);
        }
        // 记录集群节点信息
        nodeContext.clusterNodes().put(remoteNode, remoteState);
    }

    private boolean isAlive(GossipNode member) {
        return member.getState() == GossipStateEnum.UP;
    }

    private void checkDowning() {
        for (Map.Entry<GossipNode, DowningNodeState> nodeEntry : downingNodes().entrySet()) {
            GossipNode node = nodeEntry.getKey();
            DowningNodeState downingNodeState = nodeEntry.getValue();
            if (downingNodeState.getDowningCount().get() >= GossipUtil.fanOut()) {
                down(node);
                downingNodes().remove(node);
            }
        }
    }

    public boolean discoverable(GossipNode gossipNode) {
        return gossipNode.getState() == GossipStateEnum.JOIN || gossipNode.getState() == GossipStateEnum.DOWN;
    }
}
