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

    private final GossipApp gossipApp = GossipApp.instance();

    @Override
    public void run() {
        //Update local member version
        Map<GossipNode, HeartbeatState> endpointMembers = gossipApp.gossipNodeContext().endpointNodes();
        HeartbeatState heartbeatState = endpointMembers.get(gossipApp.selfNode());

        long version = heartbeatState.updateVersion();

        // 如果当前节点处于待加入集群状态，执行上线
        if (discoverable(gossipApp.selfNode())) {
            GossipApp.instance().gossipNodeContext().up(gossipApp.selfNode());
        }

        if (log.isTraceEnabled()) {
            log.trace("sync data");
            log.trace(String.format("Now my heartbeat version is %d", version));
        }

        List<GossipNodeDigest> digests = new ArrayList<>();
        try {
            randomGossipDigest(digests);
            if (!digests.isEmpty()) {
                ByteBuf byteBuf = GossipMessageCodec.codec().encodeSyncMessage(digests);
                sendBuf(byteBuf);
            }

            checkStatus();

            if (log.isTraceEnabled()) {
                log.trace("live member : {}", gossipApp.gossipNodeContext().liveNodes());
                log.trace("dead member : {}", gossipApp.gossipNodeContext().deadNodes());
                log.trace("endpoint : {}", gossipApp.gossipNodeContext().endpointNodes());
            }
//            new Thread(() -> {
//                GossipMessageHolder mm = gossipApp.messageHolder();
//                if (!mm.isEmpty()) {
//                    for (String id : mm.list()) {
//                        GossipRegularMessage msg = mm.acquire(id);
//                        int c = msg.getForwardCount();
//                        int maxTry = convergenceCount();
////                            if (isSeedNode()) {
////                                maxTry = convergenceCount();
////                            }
//                        if (c < maxTry) {
//                            ByteBuf byteBuf = GossipMessageCodec.codec().encodeRegularMessage(msg);
//                            sendBuf(byteBuf);
//                            msg.setForwardCount(c + 1);
//                        }
//                        if ((System.currentTimeMillis() - msg.getCreateTime()) >= msg.getTtl()) {
//                            mm.remove(id);
//                        }
//                    }
//                }
//            }).start();

            GossipMessageHolder mm = gossipApp.gossipMessageHolder();
            if (!mm.isEmpty()) {
                for (String id : mm.list()) {
                    GossipMessage msg = mm.acquire(id);
                    int c = msg.getForwardCount();
                    int maxTry = convergenceCount();
//                            if (isSeedNode()) {
//                                maxTry = convergenceCount();
//                            }
                    if (c < maxTry) {
                        ByteBuf byteBuf = GossipMessageCodec.codec().encodeRegularMessage(msg);
                        sendBuf(byteBuf);
                        msg.setForwardCount(c + 1);
                    }
                    if ((System.currentTimeMillis() - msg.getCreateTime()) >= msg.getTtl()) {
                        mm.remove(id);
                    }
                }
            }
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        }

    }

    private void sendBuf(ByteBuf byteBuf) {
        //gossip to some random live members
        boolean b = gossip2LiveMember(byteBuf);

        //gossip to a random dead members
        gossip2UndiscoverableMember(byteBuf);

        //
        if (!b || gossipApp.gossipNodeContext().liveNodes().size() <= gossipApp.setting().getSendNodes().size()) {
            gossip2Seed(byteBuf);
        }
    }

    private boolean discoverable(GossipNode gossipNode) {
        return gossipNode.getState() == GossipStateEnum.JOIN || gossipNode.getState() == GossipStateEnum.DOWN;
    }

    private void randomGossipDigest(List<GossipNodeDigest> digests) throws UnknownHostException {
        List<GossipNode> endpoints = new ArrayList<>(gossipApp.gossipNodeContext().endpointNodes().keySet());
        Collections.shuffle(endpoints, ThreadLocalRandom.current());

        for (GossipNode gossipNode : endpoints) {
            HeartbeatState heartbeatState = gossipApp.gossipNodeContext().endpointNodes().get(gossipNode);
            long hbTime = 0;
            long hbVersion = 0;
            if (heartbeatState != null) {
                hbTime = heartbeatState.getHeartbeatTime();
                hbVersion = heartbeatState.getVersion();
            }
            digests.add(new GossipNodeDigest(gossipNode, hbTime, hbVersion));
        }
    }

    private void checkStatus() {
        try {
            GossipNode local = gossipApp.selfNode();
            Map<GossipNode, HeartbeatState> endpoints = gossipApp.gossipNodeContext().endpointNodes();
            Set<GossipNode> epKeys = endpoints.keySet();
            for (GossipNode k : epKeys) {
                if (!k.equals(local)) {
                    HeartbeatState state = endpoints.get(k);
                    long now = System.currentTimeMillis();
                    long duration = now - state.getHeartbeatTime();
                    long convictedTime = convictedTime();
                    log.info("check1 : " + k + " state : " + state + " duration : " + duration + " convictedTime : " + convictedTime);
                    if (duration > convictedTime && (isAlive(k) || gossipApp.gossipNodeContext().liveNodes().contains(k))) {
                        downing(k, state);
                    }
                    if (duration <= convictedTime && (discoverable(k) || gossipApp.gossipNodeContext().deadNodes().contains(k))) {
                        GossipApp.instance().gossipNodeContext().up(k);
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
        int size = gossipApp.gossipNodeContext().endpointNodes().size();
        return (int) Math.floor(Math.log10(size) + Math.log(size) + 1);
    }
//
//    public static void main(String[] args) {
//        for (int size = 0; size < 10000; size++) {
//            System.out.println((int) Math.floor(Math.log10(size) + Math.log(size) + 1));
//        }
//    }

    private boolean gossip2LiveMember(ByteBuf byteBuf) {
        int liveSize = gossipApp.gossipNodeContext().liveNodes().size();
        if (liveSize == 0) {
            return false;
        }

        boolean success = false;
        int c = Math.min(liveSize, convergenceCount());
        for (int i = 0; i < c; i++) {
            int index = ThreadLocalRandom.current().nextInt(liveSize);
            success = success || sendGossip(byteBuf, gossipApp.gossipNodeContext().liveNodes(), index);
        }
        return success;
    }

    private void gossip2UndiscoverableMember(ByteBuf buffer) {
        int deadSize = gossipApp.gossipNodeContext().deadNodes().size();
        if (deadSize == 0) {
            return;
        }
        int index = (deadSize == 1) ? 0 : ThreadLocalRandom.current().nextInt(deadSize);
        sendGossip(buffer, gossipApp.gossipNodeContext().deadNodes(), index);
    }

    private void gossip2Seed(ByteBuf byteBuf) {
        int size = gossipApp.setting().getSendNodes().size();
        if (size > 0) {
            if (size == 1 && isSeedNode()) {
                return;
            }
            int index = (size == 1) ? 0 : ThreadLocalRandom.current().nextInt(size);
            if (gossipApp.gossipNodeContext().liveNodes().size() == 1) {
                sendGossip2Seed(byteBuf, gossipApp.setting().getSendNodes(), index);
            } else {
                double prob = size / (double) gossipApp.gossipNodeContext().liveNodes().size();
                if (ThreadLocalRandom.current().nextDouble() < prob) {
                    sendGossip2Seed(byteBuf, gossipApp.setting().getSendNodes(), index);
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
                if (target.equals(gossipMember2SeedMember(gossipApp.selfNode()))) {
                    if (m_size <= 1) {
                        return false;
                    } else {
                        target = members.get((index + 1) % m_size);
                    }
                }
                NetworkService messageService = gossipApp.networkService();
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
        if (gossipApp.listener() != null) {
            if (state == GossipStateEnum.RECEIVE) {
                new Thread(() -> gossipApp.listener().gossipEvent(member, state, payload)).start();
            } else {
                gossipApp.listener().gossipEvent(member, state, payload);
            }
        }
    }

    private long convictedTime() {
        long executeGossipTime = 500;
        return ((convergenceCount() * (gossipApp.setting().getNetworkDelay() * 3L + executeGossipTime)) << 1) + gossipApp.setting().getGossipInterval();
    }

    private boolean isAlive(GossipNode member) {
        return member.getState() == GossipStateEnum.UP;
    }

    private void downing(GossipNode member, HeartbeatState state) {
        log.info("downing ~~");
        try {//11
            if (gossipApp.gossipNodeContext().candidateMembers().containsKey(member)) {
                CandidateNodeState cState = gossipApp.gossipNodeContext().candidateMembers().get(member);
                if (state.getHeartbeatTime() == cState.getHeartbeatTime()) {
                    cState.updateCount();
                } else if (state.getHeartbeatTime() > cState.getHeartbeatTime()) {
                    gossipApp.gossipNodeContext().candidateMembers().remove(member);
                }
            } else {
                gossipApp.gossipNodeContext().candidateMembers().put(member, new CandidateNodeState(state.getHeartbeatTime()));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void checkCandidate() {
        Set<GossipNode> keys = gossipApp.gossipNodeContext().candidateMembers().keySet();
        for (GossipNode m : keys) {
            if (gossipApp.gossipNodeContext().candidateMembers().get(m).getDowningCount().get() >= convergenceCount()) {
                down(m);
                gossipApp.gossipNodeContext().candidateMembers().remove(m);
            }
        }
    }

    public void down(GossipNode member) {
        log.info("down ~~");
        try {
            gossipApp.lock().writeLock().lock();
            member.setState(GossipStateEnum.DOWN);
            gossipApp.gossipNodeContext().liveNodes().remove(member);
            if (!gossipApp.gossipNodeContext().deadNodes().contains(member)) {
                gossipApp.gossipNodeContext().deadNodes().add(member);
            }
            fireGossipEvent(member, GossipStateEnum.DOWN);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            gossipApp.lock().writeLock().unlock();
        }
    }

    private boolean sendGossip(ByteBuf byteBuf, List<GossipNode> nodes, int index) {
        if (byteBuf != null && index >= 0) {
            try {
                GossipNode target = nodes.get(index);
                if (target.equals(gossipApp.selfNode())) {
                    int m_size = nodes.size();
                    if (m_size == 1) {
                        return false;
                    } else {
                        target = nodes.get((index + 1) % m_size);
                    }
                }
                gossipApp.networkService().send(target.getHost(), target.getPort(), byteBuf);
                return gossipApp.setting().getSendNodes().contains(gossipMember2SeedMember(target));
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
        if (gossipApp.getSeedNode() == null) {
            return gossipApp.setting().getSendNodes().contains(gossipMember2SeedMember(gossipApp.selfNode()));
        }
        return gossipApp.getSeedNode();
    }
}