package cn.opentp.gossip.core;

import cn.opentp.gossip.GossipManagement;
import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.enums.MessageTypeEnum;
import cn.opentp.gossip.model.*;
import cn.opentp.gossip.net.MessageService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 流言传播 Task
 */
public class GossipTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(GossipTask.class);
    private final GossipManagement gossipManagement = GossipManagement.instance();


    @Override
    public void run() {
        //Update local member version
        Map<GossipNode, HeartbeatState> endpointMembers = gossipManagement.endpointMembers();
        HeartbeatState heartbeatState = endpointMembers.get(gossipManagement.selfNode());

        long version = gossipManagement.endpointMembers().get(gossipManagement.selfNode()).updateVersion();

        if (isDiscoverable(gossipManagement.selfNode())) {
            up(gossipManagement.selfNode());
        }
        if (log.isTraceEnabled()) {
            log.trace("sync data");
            log.trace(String.format("Now my heartbeat version is %d", version));
        }

        List<GossipDigest> digests = new ArrayList<>();
        try {
            randomGossipDigest(digests);
            if (digests.size() > 0) {
                ByteBuf syncMessageBuffer = encodeSyncMessage(digests);
                sendBuf(syncMessageBuffer);
            }

            checkStatus();

            if (log.isTraceEnabled()) {
                log.trace("live member : {}", gossipManagement.liveMembers());
                log.trace("dead member : {}", gossipManagement.deadMembers());
                log.trace("endpoint : {}", gossipManagement.endpointMembers());
            }
            new Thread(() -> {
                MessageManager mm = gossipManagement.getMessageManager();
                if (!mm.isEmpty()) {
                    for (String id : mm.list()) {
                        RegularMessage msg = mm.acquire(id);
                        int c = msg.getForwardCount();
                        int maxTry = convergenceCount();
//                            if (isSeedNode()) {
//                                maxTry = convergenceCount();
//                            }
                        if (c < maxTry) {
                            sendBuf(encodeRegularMessage(msg));
                            msg.setForwardCount(c + 1);
                        }
                        if ((System.currentTimeMillis() - msg.getCreateTime()) >= msg.getTtl()) {
                            mm.remove(id);
                        }
                    }
                }
            }).start();
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        }

    }

    private void sendBuf(ByteBuf buf) {
        //step 1. goosip to some random live members
        boolean b = gossip2LiveMember(buf);

        //step 2. goosip to a random dead memeber
        gossip2UndiscoverableMember(buf);

        //step3.
        if (!b || gossipManagement.liveMembers().size() <= gossipManagement.setting().getSendNodes().size()) {
            gossip2Seed(buf);
        }
    }


    private void up(GossipNode member) {
        try {
            gossipManagement.globalLock().writeLock().lock();
            member.setState(GossipStateEnum.UP);
            if (!gossipManagement.liveMembers().contains(member)) {
                gossipManagement.liveMembers().add(member);
            }
            if (gossipManagement.candidateMembers().containsKey(member)) {
                gossipManagement.candidateMembers().remove(member);
            }
            if (gossipManagement.deadMembers().contains(member)) {
                gossipManagement.deadMembers().remove(member);
                log.info("up ~~");
                if (!member.equals(gossipManagement.selfNode())) {
                    fireGossipEvent(member, GossipStateEnum.UP);
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            gossipManagement.globalLock().writeLock().unlock();
        }

    }

    private boolean isDiscoverable(GossipNode member) {
        return member.getState() == GossipStateEnum.JOIN || member.getState() == GossipStateEnum.DOWN;
    }

    private void randomGossipDigest(List<GossipDigest> digests) throws UnknownHostException {
        List<GossipNode> endpoints = new ArrayList<>(gossipManagement.endpointMembers().keySet());
        Collections.shuffle(endpoints, gossipManagement.getRandom());
        for (GossipNode ep : endpoints) {
            HeartbeatState hb = gossipManagement.endpointMembers().get(ep);
            long hbTime = 0;
            long hbVersion = 0;
            if (hb != null) {
                hbTime = hb.getHeartbeatTime();
                hbVersion = hb.getVersion();
            }
            digests.add(new GossipDigest(ep, hbTime, hbVersion));
        }
    }

    private ByteBuf encodeSyncMessage(List<GossipDigest> digests) {

        JSONArray array = new JSONArray();
        for (GossipDigest e : digests) {
            array.add(Serializer.getInstance().encode(e).toString());
        }

        JSONObject jsonObject = GossipMessageFactory.getInstance().makeMessage(MessageTypeEnum.SYNC_MESSAGE, array.toJSONString(), gossipManagement.setting().getCluster(), gossipManagement.selfNode().socketAddress());
        return Unpooled.copiedBuffer(jsonObject.toString(), StandardCharsets.UTF_8);
    }

    private void checkStatus() {
        try {
            GossipNode local = gossipManagement.selfNode();
            Map<GossipNode, HeartbeatState> endpoints = gossipManagement.endpointMembers();
            Set<GossipNode> epKeys = endpoints.keySet();
            for (GossipNode k : epKeys) {
                if (!k.equals(local)) {
                    HeartbeatState state = endpoints.get(k);
                    long now = System.currentTimeMillis();
                    long duration = now - state.getHeartbeatTime();
                    long convictedTime = convictedTime();
                    log.info("check : " + k + " state : " + state + " duration : " + duration + " convictedTime : " + convictedTime);
                    if (duration > convictedTime && (isAlive(k) || gossipManagement.liveMembers().contains(k))) {
                        downing(k, state);
                    }
                    if (duration <= convictedTime && (isDiscoverable(k) || gossipManagement.deadMembers().contains(k))) {
                        up(k);
                    }
                }
            }
            checkCandidate();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private int convergenceCount() {
        int size = gossipManagement.endpointMembers().size();
        return (int) Math.floor(Math.log10(size) + Math.log(size) + 1);
    }

    private ByteBuf encodeRegularMessage(RegularMessage regularMessage) {

        String msg = JSON.toJSONString(regularMessage);

        JSONObject jsonObject = GossipMessageFactory.getInstance().makeMessage(MessageTypeEnum.REG_MESSAGE, msg, gossipManagement.setting().getCluster(), gossipManagement.selfNode().socketAddress());

        return Unpooled.copiedBuffer(jsonObject.toJSONString(), StandardCharsets.UTF_8);
    }

    private boolean gossip2LiveMember(ByteBuf buffer) {
        int liveSize = gossipManagement.liveMembers().size();
        if (liveSize <= 0) {
            return false;
        }
        boolean b = false;
        int c = Math.min(liveSize, convergenceCount());
        for (int i = 0; i < c; i++) {
            int index = gossipManagement.getRandom().nextInt(liveSize);
            b = b || sendGossip(buffer, gossipManagement.liveMembers(), index);
        }
        return b;
    }

    private void gossip2UndiscoverableMember(ByteBuf buffer) {
        int deadSize = gossipManagement.deadMembers().size();
        if (deadSize <= 0) {
            return;
        }
        int index = (deadSize == 1) ? 0 : gossipManagement.getRandom().nextInt(deadSize);
        sendGossip(buffer, gossipManagement.deadMembers(), index);
    }

    private void gossip2Seed(ByteBuf buffer) {
        int size = gossipManagement.setting().getSendNodes().size();
        if (size > 0) {
            if (size == 1 && isSeedNode()) {
                return;
            }
            int index = (size == 1) ? 0 : gossipManagement.getRandom().nextInt(size);
            System.out.println("index = " + index);
            if (gossipManagement.liveMembers().size() == 1) {
                sendGossip2Seed(buffer, gossipManagement.setting().getSendNodes(), index);
            } else {
                double prob = size / (double) gossipManagement.liveMembers().size();
                if (gossipManagement.getRandom().nextDouble() < prob) {
                    sendGossip2Seed(buffer, gossipManagement.setting().getSendNodes(), index);
                }
            }
        }
    }

    private boolean sendGossip2Seed(ByteBuf buffer, List<SeedNode> members, int index) {
        if (buffer != null && index >= 0) {
            try {
                SeedNode target = members.get(index);
                int m_size = members.size();
                if (target.equals(gossipMember2SeedMember(gossipManagement.selfNode()))) {
                    if (m_size <= 1) {
                        return false;
                    } else {
                        target = members.get((index + 1) % m_size);
                    }
                }
                MessageService messageService = gossipManagement.messageService();
                messageService.send(target.getHost(), target.getPort(), buffer);
                return true;
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return false;
    }

    private void fireGossipEvent(GossipNode member, GossipStateEnum state) {
        fireGossipEvent(member, state, null);
    }

    public void fireGossipEvent(GossipNode member, GossipStateEnum state, Object payload) {
        if (gossipManagement.getListener() != null) {
            if (state == GossipStateEnum.RECEIVE) {
                new Thread(() -> gossipManagement.getListener().gossipEvent(member, state, payload)).start();
            } else {
                gossipManagement.getListener().gossipEvent(member, state, payload);
            }
        }
    }

    private long convictedTime() {
        long executeGossipTime = 500;
        return ((convergenceCount() * (gossipManagement.setting().getNetworkDelay() * 3L + executeGossipTime)) << 1) + gossipManagement.setting().getGossipInterval();
    }

    private boolean isAlive(GossipNode member) {
        return member.getState() == GossipStateEnum.UP;
    }

    private void downing(GossipNode member, HeartbeatState state) {
        log.info("downing ~~");
        try {//11
            if (gossipManagement.candidateMembers().containsKey(member)) {
                CandidateMemberState cState = gossipManagement.candidateMembers().get(member);
                if (state.getHeartbeatTime() == cState.getHeartbeatTime()) {
                    cState.updateCount();
                } else if (state.getHeartbeatTime() > cState.getHeartbeatTime()) {
                    gossipManagement.candidateMembers().remove(member);
                }
            } else {
                gossipManagement.candidateMembers().put(member, new CandidateMemberState(state.getHeartbeatTime()));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void checkCandidate() {
        Set<GossipNode> keys = gossipManagement.candidateMembers().keySet();
        for (GossipNode m : keys) {
            if (gossipManagement.candidateMembers().get(m).getDowningCount().get() >= convergenceCount()) {
                down(m);
                gossipManagement.candidateMembers().remove(m);
            }
        }
    }

    public void down(GossipNode member) {
        log.info("down ~~");
        try {//
            gossipManagement.globalLock().writeLock().lock();
            member.setState(GossipStateEnum.DOWN);
            gossipManagement.liveMembers().remove(member);
            if (!gossipManagement.deadMembers().contains(member)) {
                gossipManagement.deadMembers().add(member);
            }
//            clearExecutor.schedule(() -> clearMember(member), getSettings().getDeleteThreshold() * getSettings().getGossipInterval(), TimeUnit.MILLISECONDS);
            fireGossipEvent(member, GossipStateEnum.DOWN);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            gossipManagement.globalLock().writeLock().unlock();
        }
    }

    private boolean sendGossip(ByteBuf buffer, List<GossipNode> members, int index) {
        if (buffer != null && index >= 0) {
            try {
                GossipNode target = members.get(index);
                if (target.equals(gossipManagement.selfNode())) {
                    int m_size = members.size();
                    if (m_size == 1) {
                        return false;
                    } else {
                        target = members.get((index + 1) % m_size);
                    }
                }
                gossipManagement.messageService().send(target.getHost(), target.getPort(), buffer);
                return gossipManagement.setting().getSendNodes().contains(gossipMember2SeedMember(target));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return false;
    }

    private SeedNode gossipMember2SeedMember(GossipNode member) {
        return new SeedNode(member.getCluster(), member.getNodeId(), member.getHost(), member.getPort());
    }

    public boolean isSeedNode() {
        if (gossipManagement.getSeedNode() == null) {
            return gossipManagement.setting().getSendNodes().contains(gossipMember2SeedMember(gossipManagement.selfNode()));
        }
        return gossipManagement.getSeedNode();
    }

    //    private void clearMember(GossipMember member) {
//        rwlock.writeLock().lock();
//        try {
//            endpointMembers.remove(member);
//        } finally {
//            rwlock.writeLock().unlock();
//        }
//    }
}