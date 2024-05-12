package cn.opentp.gossip.node;

import cn.opentp.gossip.GossipApp;
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

public class GossipNodeContext {

    private static final Logger log = LoggerFactory.getLogger(GossipNodeContext.class);

    // 所有节点
    private final Map<GossipNode, HeartbeatState> endpointNodes = new ConcurrentHashMap<>();
    // 活节点
    private final List<GossipNode> liveNodes = new CopyOnWriteArrayList<>();
    // 死节点
    private final List<GossipNode> deadNodes = new CopyOnWriteArrayList<>();
    // 候选节点，判定中的节点
    private final Map<GossipNode, CandidateNodeState> candidateMembers = new ConcurrentHashMap<>();

    public Map<GossipNode, HeartbeatState> endpointNodes() {
        return endpointNodes;
    }

    public List<GossipNode> liveNodes() {
        return liveNodes;
    }

    public List<GossipNode> deadNodes() {
        return deadNodes;
    }

    public Map<GossipNode, CandidateNodeState> candidateMembers() {
        return candidateMembers;
    }

    /**
     * 节点上线
     *
     * @param node 上线节点
     */
    public void up(GossipNode node) {
        ReentrantReadWriteLock.WriteLock writeLock = GossipApp.instance().lock().writeLock();
        try {
            writeLock.lock();
            node.setState(GossipStateEnum.UP);
            // 非原子操作
            if (!liveNodes().contains(node)) {
                liveNodes().add(node);
            }
            // 候选节点移除
            candidateMembers().remove(node);
            // 终止节点移除
            deadNodes().remove(node);

            if (!node.equals(GossipApp.instance().selfNode())) {
                // 触发上线事件
                GossipApp.instance().gossipListenerContext().fireGossipEvent(node, GossipStateEnum.UP);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            writeLock.unlock();
        }
    }

    public void downing(GossipNode member, HeartbeatState state) {
        log.info("downing ~~");
        try {//11
            if (GossipApp.instance().gossipNodeContext().candidateMembers().containsKey(member)) {
                CandidateNodeState cState = GossipApp.instance().gossipNodeContext().candidateMembers().get(member);
                if (state.getHeartbeatTime() == cState.getHeartbeatTime()) {
                    cState.updateCount();
                } else if (state.getHeartbeatTime() > cState.getHeartbeatTime()) {
                    GossipApp.instance().gossipNodeContext().candidateMembers().remove(member);
                }
            } else {
                GossipApp.instance().gossipNodeContext().candidateMembers().put(member, new CandidateNodeState(state.getHeartbeatTime()));
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
        ReentrantReadWriteLock.WriteLock writeLock = GossipApp.instance().lock().writeLock();
        try {
            writeLock.lock();
            node.setState(GossipStateEnum.DOWN);
            // 活动节点移除
            liveNodes().remove(node);
            // 加入终止节点
            if (!deadNodes().contains(node)) {
                deadNodes().add(node);
            }
            // 触发下线事件
            GossipApp.instance().gossipListenerContext().fireGossipEvent(node, GossipStateEnum.DOWN);
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
        GossipApp gossipApp = GossipApp.instance();
        GossipNode selfNode = gossipApp.selfNode();
        ByteBuf byteBuf = GossipMessageCodec.codec().encodeShutdownMessage(selfNode);
        for (GossipNode node : liveNodes()) {
            try {
                if (!node.equals(selfNode)) {
                    gossipApp.networkService().send(node.getHost(), node.getPort(), byteBuf);
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
     * @throws UnknownHostException
     */
    public List<GossipNodeDigest> randomGossipNodeDigest() throws UnknownHostException {
        List<GossipNodeDigest> nodeDigests = new ArrayList<>();

        GossipNodeContext nodeContext = GossipApp.instance().gossipNodeContext();

        List<GossipNode> nodes = new ArrayList<>(nodeContext.endpointNodes().keySet());
        Collections.shuffle(nodes, ThreadLocalRandom.current());

        for (GossipNode node : nodes) {
            HeartbeatState heartbeatState = nodeContext.endpointNodes().get(node);
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

    public void checkStatus() {
        try {
            GossipNode local = GossipApp.instance().selfNode();
            GossipNodeContext nodeContext = GossipApp.instance().gossipNodeContext();
            Map<GossipNode, HeartbeatState> endpoints = GossipApp.instance().gossipNodeContext().endpointNodes();
            Set<GossipNode> epKeys = endpoints.keySet();
            for (GossipNode k : epKeys) {
                if (!k.equals(local)) {
                    HeartbeatState state = endpoints.get(k);
                    long now = System.currentTimeMillis();
                    long duration = now - state.getHeartbeatTime();
                    long convictedTime = GossipUtil.convictedTime();
                    log.info("check1 : " + k + " state : " + state + " duration : " + duration + " convictedTime : " + convictedTime);
                    if (duration > convictedTime && (isAlive(k) || GossipApp.instance().gossipNodeContext().liveNodes().contains(k))) {
                        downing(k, state);
                    }
                    if (duration <= convictedTime && (discoverable(k) || GossipApp.instance().gossipNodeContext().deadNodes().contains(k))) {
                        up(k);
                    }
                }
            }
            checkCandidate();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private boolean isAlive(GossipNode member) {
        return member.getState() == GossipStateEnum.UP;
    }

    private void checkCandidate() {
        GossipNodeContext nodeContext = GossipApp.instance().gossipNodeContext();
        Set<GossipNode> keys = GossipApp.instance().gossipNodeContext().candidateMembers().keySet();
        for (GossipNode m : keys) {
            if (GossipApp.instance().gossipNodeContext().candidateMembers().get(m).getDowningCount().get() >= GossipUtil.fanOut()) {
                nodeContext.down(m);
                GossipApp.instance().gossipNodeContext().candidateMembers().remove(m);
            }
        }
    }

    public boolean discoverable(GossipNode gossipNode) {
        return gossipNode.getState() == GossipStateEnum.JOIN || gossipNode.getState() == GossipStateEnum.DOWN;
    }
}
