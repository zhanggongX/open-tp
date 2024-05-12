package cn.opentp.gossip.schedule;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.message.GossipMessage;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
import cn.opentp.gossip.message.holder.GossipMessageHolder;
import cn.opentp.gossip.node.*;
import cn.opentp.gossip.network.NetworkService;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.*;
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
        Map<GossipNode, HeartbeatState> endpointMembers = nodeContext.endpointNodes();
        HeartbeatState heartbeatState = endpointMembers.get(gossipApp.selfNode());

        long version = heartbeatState.updateVersion();
        log.debug("heartbeat version is {}", version);

        // 如果当前节点处于待加入集群状态，执行上线
        GossipNode selfNode = gossipApp.selfNode();
        if (discoverable(selfNode)) {
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

        checkStatus();

        log.trace("live nodes : {}", nodeContext.liveNodes());
        log.trace("dead nodes : {}", nodeContext.deadNodes());
        log.trace("cluster nodes : {}", nodeContext.endpointNodes());

        // 处理流言信息
        GossipMessageHolder messageHolder = gossipApp.gossipMessageHolder();
        if (messageHolder.isEmpty()) {
            return;
        }
        for (String messageId : messageHolder.list()) {
            GossipMessage message = messageHolder.acquire(messageId);
            int forwardCount = message.getForwardCount();
            int maxTry = convergenceCount();
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
        //gossip to some random live members
        boolean b = gossip2LiveMember(byteBuf);

        //gossip to a random dead members
        gossip2UndiscoverableMember(byteBuf);

        //
        if (!b || GossipApp.instance().gossipNodeContext().liveNodes().size() <= GossipApp.instance().setting().getSendNodes().size()) {
            gossip2Seed(byteBuf);
        }
    }

    private boolean discoverable(GossipNode gossipNode) {
        return gossipNode.getState() == GossipStateEnum.JOIN || gossipNode.getState() == GossipStateEnum.DOWN;
    }

    private void checkStatus() {
        try {
            GossipNode local = GossipApp.instance().selfNode();
            Map<GossipNode, HeartbeatState> endpoints = GossipApp.instance().gossipNodeContext().endpointNodes();
            Set<GossipNode> epKeys = endpoints.keySet();
            for (GossipNode k : epKeys) {
                if (!k.equals(local)) {
                    HeartbeatState state = endpoints.get(k);
                    long now = System.currentTimeMillis();
                    long duration = now - state.getHeartbeatTime();
                    long convictedTime = convictedTime();
                    log.info("check1 : " + k + " state : " + state + " duration : " + duration + " convictedTime : " + convictedTime);
                    if (duration > convictedTime && (isAlive(k) || GossipApp.instance().gossipNodeContext().liveNodes().contains(k))) {
                        downing(k, state);
                    }
                    if (duration <= convictedTime && (discoverable(k) || GossipApp.instance().gossipNodeContext().deadNodes().contains(k))) {
                        GossipApp.instance().instance().gossipNodeContext().up(k);
                    }
                }
            }
            checkCandidate();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private int convergenceCount() {
        // 计算流言传播度
        int size = GossipApp.instance().gossipNodeContext().endpointNodes().size();
        return (int) Math.floor(Math.log10(size) + Math.log(size) + 1);
    }
//
//    public static void main(String[] args) {
//        for (int size = 0; size < 10000; size++) {
//            System.out.println((int) Math.floor(Math.log10(size) + Math.log(size) + 1));
//        }
//    }

    private boolean gossip2LiveMember(ByteBuf byteBuf) {
        int liveSize = GossipApp.instance().gossipNodeContext().liveNodes().size();
        if (liveSize == 0) {
            return false;
        }

        boolean success = false;
        int c = Math.min(liveSize, convergenceCount());
        for (int i = 0; i < c; i++) {
            int index = ThreadLocalRandom.current().nextInt(liveSize);
            success = success || sendGossip(byteBuf, GossipApp.instance().gossipNodeContext().liveNodes(), index);
        }
        return success;
    }

    private void gossip2UndiscoverableMember(ByteBuf buffer) {
        int deadSize = GossipApp.instance().gossipNodeContext().deadNodes().size();
        if (deadSize == 0) {
            return;
        }
        int index = (deadSize == 1) ? 0 : ThreadLocalRandom.current().nextInt(deadSize);
        sendGossip(buffer, GossipApp.instance().gossipNodeContext().deadNodes(), index);
    }

    private void gossip2Seed(ByteBuf byteBuf) {
        int size = GossipApp.instance().setting().getSendNodes().size();
        if (size > 0) {
            if (size == 1 && isSeedNode()) {
                return;
            }
            int index = (size == 1) ? 0 : ThreadLocalRandom.current().nextInt(size);
            if (GossipApp.instance().gossipNodeContext().liveNodes().size() == 1) {
                sendGossip2Seed(byteBuf, GossipApp.instance().setting().getSendNodes(), index);
            } else {
                double prob = size / (double) GossipApp.instance().gossipNodeContext().liveNodes().size();
                if (ThreadLocalRandom.current().nextDouble() < prob) {
                    sendGossip2Seed(byteBuf, GossipApp.instance().setting().getSendNodes(), index);
                }
            }
        }
    }

    private boolean sendGossip2Seed(ByteBuf byteBuf, List<SeedNode> members, int index) {
        if (byteBuf != null && index >= 0) {
//            ByteBuf sendByteBuf = byteBuf.copy();
            try {
                SeedNode target = members.get(index);
                int m_size = members.size();
                if (target.equals(gossipMember2SeedMember(GossipApp.instance().selfNode()))) {
                    if (m_size <= 1) {
                        return false;
                    } else {
                        target = members.get((index + 1) % m_size);
                    }
                }
                NetworkService messageService = GossipApp.instance().networkService();
                messageService.send(target.getHost(), target.getPort(), byteBuf);
                return true;
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return false;
    }

    private void fireGossipEvent(GossipNode member, GossipStateEnum state) {
        fireGossipEvent(member, state, null);
    }

    public void fireGossipEvent(GossipNode member, GossipStateEnum state, Object payload) {
        if (GossipApp.instance().listener() != null) {
            if (state == GossipStateEnum.RECEIVE) {
                new Thread(() -> GossipApp.instance().listener().gossipEvent(member, state, payload)).start();
            } else {
                GossipApp.instance().listener().gossipEvent(member, state, payload);
            }
        }
    }

    private long convictedTime() {
        long executeGossipTime = 500;
        return ((convergenceCount() * (GossipApp.instance().setting().getNetworkDelay() * 3L + executeGossipTime)) << 1) + GossipApp.instance().setting().getGossipInterval();
    }

    private boolean isAlive(GossipNode member) {
        return member.getState() == GossipStateEnum.UP;
    }

    private void downing(GossipNode member, HeartbeatState state) {
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

    private void checkCandidate() {
        Set<GossipNode> keys = GossipApp.instance().gossipNodeContext().candidateMembers().keySet();
        for (GossipNode m : keys) {
            if (GossipApp.instance().gossipNodeContext().candidateMembers().get(m).getDowningCount().get() >= convergenceCount()) {
                down(m);
                GossipApp.instance().gossipNodeContext().candidateMembers().remove(m);
            }
        }
    }

    public void down(GossipNode member) {
        log.info("down ~~");
        try {
            GossipApp.instance().lock().writeLock().lock();
            member.setState(GossipStateEnum.DOWN);
            GossipApp.instance().gossipNodeContext().liveNodes().remove(member);
            if (!GossipApp.instance().gossipNodeContext().deadNodes().contains(member)) {
                GossipApp.instance().gossipNodeContext().deadNodes().add(member);
            }
            fireGossipEvent(member, GossipStateEnum.DOWN);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            GossipApp.instance().lock().writeLock().unlock();
        }
    }

    private boolean sendGossip(ByteBuf byteBuf, List<GossipNode> nodes, int index) {
        if (byteBuf != null && index >= 0) {
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
                return GossipApp.instance().setting().getSendNodes().contains(gossipMember2SeedMember(target));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return false;
    }

    private SeedNode gossipMember2SeedMember(GossipNode gossipNode) {
        return new SeedNode(gossipNode.getCluster(), gossipNode.getNodeId(), gossipNode.getHost(), gossipNode.getPort());
    }

    public boolean isSeedNode() {
        if (GossipApp.instance().getSeedNode() == null) {
            return GossipApp.instance().setting().getSendNodes().contains(gossipMember2SeedMember(GossipApp.instance().selfNode()));
        }
        return GossipApp.instance().getSeedNode();
    }
}