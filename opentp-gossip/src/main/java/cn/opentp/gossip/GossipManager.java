package cn.opentp.gossip;

import cn.opentp.gossip.core.GossipMessageFactory;
import cn.opentp.gossip.core.InMemMessageManager;
import cn.opentp.gossip.core.MessageManager;
import cn.opentp.gossip.core.Serializer;
import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.enums.MessageTypeEnum;
import cn.opentp.gossip.event.DefaultGossipListener;
import cn.opentp.gossip.event.GossipListener;
import cn.opentp.gossip.model.*;
import cn.opentp.gossip.net.MessageService;
import cn.opentp.gossip.net.udp.UDPMessageService;
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
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Gossip 全局唯一实例
 */
public class GossipManager {

    private static final Logger log = LoggerFactory.getLogger(GossipManager.class);
    // 实例
    private static final GossipManager INSTANCE = new GossipManager();

    private final ReentrantReadWriteLock globalLock = new ReentrantReadWriteLock();
    private final ScheduledExecutorService gossipScheduleExecutor = Executors.newScheduledThreadPool(1);

    private final MessageService messageService = new UDPMessageService();

    private boolean hadInit = false;
    private boolean isWorking = false;
    private Boolean isSeedNode = null;

    private final Map<GossipNode, HeartbeatState> endpointMembers = new ConcurrentHashMap<>();
    private final List<GossipNode> liveMembers = new CopyOnWriteArrayList<>();
    private final List<GossipNode> deadMembers = new CopyOnWriteArrayList<>();
    private final Map<GossipNode, CandidateMemberState> candidateMembers = new ConcurrentHashMap<>();

    private GossipSettings gossipSettings = new GossipSettings();
    private GossipListener listener = new DefaultGossipListener();

    private final Random random = new Random();

    private MessageManager messageManager = new InMemMessageManager();


    private GossipManager() {
    }

    public static GossipManager instance() {
        return INSTANCE;
    }

//    public void init(String cluster, String ipAddress, Integer port, String id, List<SeedNode> seedMembers, GossipSettings settings, GossipListener listener) {
//        this.cluster = cluster;
//        this.localGossipMember = new GossipMember();
//        this.localGossipMember.setCluster(cluster);
//        this.localGossipMember.setIpAddress(ipAddress);
//        this.localGossipMember.setPort(port);
//        this.localGossipMember.setId(id);
//        this.localGossipMember.setState(GossipStateEnum.JOIN);
//        this.endpointMembers.put(localGossipMember, new HeartbeatState());
//        this.listener = listener;
//        this.settings = settings;
//        this.settings.setSeedMembers(seedMembers);
//        fireGossipEvent(localGossipMember, GossipStateEnum.JOIN);
//    }

    public MessageService messageService() {
        return messageService;
    }

    public List<GossipNode> getLiveMembers() {
        return liveMembers;
    }

    public List<GossipNode> getDeadMembers() {
        return deadMembers;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public Map<GossipNode, HeartbeatState> endpointMembers() {
        return endpointMembers;
    }

    private void randomGossipDigest(List<GossipDigest> digests) throws UnknownHostException {
        List<GossipNode> endpoints = new ArrayList<>(endpointMembers.keySet());
        Collections.shuffle(endpoints, random);
        for (GossipNode ep : endpoints) {
            HeartbeatState hb = endpointMembers.get(ep);
            long hbTime = 0;
            long hbVersion = 0;
            if (hb != null) {
                hbTime = hb.getHeartbeatTime();
                hbVersion = hb.getVersion();
            }
            digests.add(new GossipDigest(ep, hbTime, hbVersion));
        }
    }

    public void setGossipListener(GossipListener gossipListener) {
        listener = gossipListener;
    }

    public void setWorking() {
        isWorking = true;
    }

    public void startListen() {
        messageService.listen(setting().getHost(), setting().getPort());
    }

    public void startTask() {
        gossipScheduleExecutor.scheduleAtFixedRate(new GossipManager.GossipTask(), setting().getGossipInterval(), setting().getGossipInterval(), TimeUnit.MILLISECONDS);
    }

    public void initMark() {
        hadInit = true;
    }


    class GossipTask implements Runnable {

        @Override
        public void run() {
            //Update local member version
            long version = endpointMembers.get(selfNode()).updateVersion();
            if (isDiscoverable(selfNode())) {
                up(selfNode());
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
                    log.trace("live member : " + getLiveMembers());
                    log.trace("dead member : " + getDeadMembers());
                    log.trace("endpoint : " + endpointMembers());
                }
                new Thread(() -> {
                    MessageManager mm = messageManager;
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
            if (!b || liveMembers.size() <= setting().getSendNodes().size()) {
                gossip2Seed(buf);
            }
        }
    }

    private ByteBuf encodeSyncMessage(List<GossipDigest> digests) {

        JSONArray array = new JSONArray();
        for (GossipDigest e : digests) {
            array.add(Serializer.getInstance().encode(e).toString());
        }

        JSONObject jsonObject = GossipMessageFactory.getInstance().makeMessage(MessageTypeEnum.SYNC_MESSAGE, array.toJSONString(), setting().getCluster(), selfNode().socketAddress());
        return Unpooled.copiedBuffer(jsonObject.toString(), StandardCharsets.UTF_8);
    }

    public ByteBuf encodeAckMessage(AckMessage ackMessage) {
        String ackJson = JSON.toJSONString(ackMessage);
        JSONObject jsonObject = GossipMessageFactory.getInstance().makeMessage(MessageTypeEnum.ACK_MESSAGE, ackJson, setting().getCluster(), selfNode().socketAddress());
        return Unpooled.copiedBuffer(jsonObject.toString(), StandardCharsets.UTF_8);
    }

    public ByteBuf encodeAck2Message(Ack2Message ack2Message) {
        String ack2Json = JSON.toJSONString(ack2Message);
        JSONObject jsonObject = GossipMessageFactory.getInstance().makeMessage(MessageTypeEnum.ACK2_MESSAGE, ack2Json, setting().getCluster(), selfNode().socketAddress());
        return Unpooled.copiedBuffer(jsonObject.toString(), StandardCharsets.UTF_8);
    }

    private ByteBuf encodeShutdownMessage() {
        String self = JSON.toJSONString(selfNode());
        JSONObject jsonObject = GossipMessageFactory.getInstance().makeMessage(MessageTypeEnum.SHUTDOWN, self, setting().getCluster(), selfNode().socketAddress());
        return Unpooled.copiedBuffer(jsonObject.toJSONString(), StandardCharsets.UTF_8);
    }

    private ByteBuf encodeRegularMessage(RegularMessage regularMessage) {

        String msg = JSON.toJSONString(regularMessage);

        JSONObject jsonObject = GossipMessageFactory.getInstance().makeMessage(MessageTypeEnum.REG_MESSAGE, msg, setting().getCluster(), selfNode().socketAddress());

        return Unpooled.copiedBuffer(jsonObject.toJSONString(), StandardCharsets.UTF_8);
    }

    public GossipNode selfNode() {
        return setting().getLocalGossipMember();
    }

    public void apply2LocalState(Map<GossipNode, HeartbeatState> endpointMembers) {
        Set<GossipNode> keys = endpointMembers.keySet();
        for (GossipNode m : keys) {
            if (selfNode().equals(m)) {
                continue;
            }

            try {
                HeartbeatState localState = endpointMembers().get(m);
                HeartbeatState remoteState = endpointMembers.get(m);

                if (localState != null) {
                    long localHeartbeatTime = localState.getHeartbeatTime();
                    long remoteHeartbeatTime = remoteState.getHeartbeatTime();
                    if (remoteHeartbeatTime > localHeartbeatTime) {
                        remoteStateReplaceLocalState(m, remoteState);
                    } else if (remoteHeartbeatTime == localHeartbeatTime) {
                        long localVersion = localState.getVersion();
                        long remoteVersion = remoteState.getVersion();
                        if (remoteVersion > localVersion) {
                            remoteStateReplaceLocalState(m, remoteState);
                        }
                    }
                } else {
                    remoteStateReplaceLocalState(m, remoteState);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private void remoteStateReplaceLocalState(GossipNode member, HeartbeatState remoteState) {
        if (member.getState() == GossipStateEnum.UP) {
            up(member);
        }
        if (member.getState() == GossipStateEnum.DOWN) {
            down(member);
        }
        if (endpointMembers.containsKey(member)) {
            endpointMembers.remove(member);
        }
        endpointMembers.put(member, remoteState);
    }

    public GossipNode createByDigest(GossipDigest digest) {
        GossipNode member = new GossipNode();
        member.setPort(digest.getEndpoint().getPort());
        member.setHost(digest.getEndpoint().getAddress().getHostAddress());
        member.setCluster(setting().getCluster());

        Set<GossipNode> keys = endpointMembers().keySet();
        for (GossipNode m : keys) {
            if (m.equals(member)) {
                member.setNodeId(m.getNodeId());
                member.setState(m.getState());
                break;
            }
        }

        return member;
    }

    /**
     * send sync message to some live members
     *
     * @param buffer sync data
     * @return if send to a seed member then return TURE
     */
    private boolean gossip2LiveMember(ByteBuf buffer) {
        int liveSize = liveMembers.size();
        if (liveSize <= 0) {
            return false;
        }
        boolean b = false;
        int c = Math.min(liveSize, convergenceCount());
        for (int i = 0; i < c; i++) {
            int index = random.nextInt(liveSize);
            b = b || sendGossip(buffer, liveMembers, index);
        }
        return b;
    }

    /**
     * send sync message to a dead member
     *
     * @param buffer sync data
     */
    private void gossip2UndiscoverableMember(ByteBuf buffer) {
        int deadSize = deadMembers.size();
        if (deadSize <= 0) {
            return;
        }
        int index = (deadSize == 1) ? 0 : random.nextInt(deadSize);
        sendGossip(buffer, deadMembers, index);
    }

    private void gossip2Seed(ByteBuf buffer) {
        int size = setting().getSendNodes().size();
        if (size > 0) {
            if (size == 1 && isSeedNode()) {
                return;
            }
            int index = (size == 1) ? 0 : random.nextInt(size);
            System.out.println("index = " + index);
            if (liveMembers.size() == 1) {
                sendGossip2Seed(buffer, setting().getSendNodes(), index);
            } else {
                double prob = size / (double) liveMembers.size();
                if (random.nextDouble() < prob) {
                    sendGossip2Seed(buffer, setting().getSendNodes(), index);
                }
            }
        }
    }

    private boolean sendGossip(ByteBuf buffer, List<GossipNode> members, int index) {
        if (buffer != null && index >= 0) {
            try {
                GossipNode target = members.get(index);
                if (target.equals(selfNode())) {
                    int m_size = members.size();
                    if (m_size == 1) {
                        return false;
                    } else {
                        target = members.get((index + 1) % m_size);
                    }
                }
                messageService.sendMsg(target.getHost(), target.getPort(), buffer);
                return setting().getSendNodes().contains(gossipMember2SeedMember(target));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return false;
    }

    private boolean sendGossip2Seed(ByteBuf buffer, List<SeedNode> members, int index) {
        if (buffer != null && index >= 0) {
            try {
                SeedNode target = members.get(index);
                int m_size = members.size();
                if (target.equals(gossipMember2SeedMember(selfNode()))) {
                    if (m_size <= 1) {
                        return false;
                    } else {
                        target = members.get((index + 1) % m_size);
                    }
                }
                messageService.sendMsg(target.getHost(), target.getPort(), buffer);
                return true;
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return false;
    }

    private SeedNode gossipMember2SeedMember(GossipNode member) {
        return new SeedNode(member.getCluster(), member.getNodeId(), member.getHost(), member.getPort());
    }

    private void checkStatus() {
        try {
            GossipNode local = selfNode();
            Map<GossipNode, HeartbeatState> endpoints = endpointMembers();
            Set<GossipNode> epKeys = endpoints.keySet();
            for (GossipNode k : epKeys) {
                if (!k.equals(local)) {
                    HeartbeatState state = endpoints.get(k);
                    long now = System.currentTimeMillis();
                    long duration = now - state.getHeartbeatTime();
                    long convictedTime = convictedTime();
                    log.info("check : " + k + " state : " + state + " duration : " + duration + " convictedTime : " + convictedTime);
                    if (duration > convictedTime && (isAlive(k) || getLiveMembers().contains(k))) {
                        downing(k, state);
                    }
                    if (duration <= convictedTime && (isDiscoverable(k) || getDeadMembers().contains(k))) {
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
        int size = endpointMembers().size();
        return (int) Math.floor(Math.log10(size) + Math.log(size) + 1);
    }

    private long convictedTime() {
        long executeGossipTime = 500;
        return ((convergenceCount() * (setting().getNetworkDelay() * 3L + executeGossipTime)) << 1) + setting().getGossipInterval();
    }

    private boolean isDiscoverable(GossipNode member) {
        return member.getState() == GossipStateEnum.JOIN || member.getState() == GossipStateEnum.DOWN;
    }

    private boolean isAlive(GossipNode member) {
        return member.getState() == GossipStateEnum.UP;
    }

    public boolean isSeedNode() {
        if (isSeedNode == null) {
            isSeedNode = setting().getSendNodes().contains(gossipMember2SeedMember(selfNode()));
        }
        return isSeedNode;
    }

    public GossipListener getListener() {
        return listener;
    }

    private void fireGossipEvent(GossipNode member, GossipStateEnum state) {
        fireGossipEvent(member, state, null);
    }

    public void fireGossipEvent(GossipNode member, GossipStateEnum state, Object payload) {
        if (getListener() != null) {
            if (state == GossipStateEnum.RCV) {
                new Thread(() -> getListener().gossipEvent(member, state, payload)).start();
            } else {
                getListener().gossipEvent(member, state, payload);
            }
        }
    }

//    private void clearMember(GossipMember member) {
//        rwlock.writeLock().lock();
//        try {
//            endpointMembers.remove(member);
//        } finally {
//            rwlock.writeLock().unlock();
//        }
//    }

    public void down(GossipNode member) {
        log.info("down ~~");
        try {//
            globalLock.writeLock().lock();
            member.setState(GossipStateEnum.DOWN);
            liveMembers.remove(member);
            if (!deadMembers.contains(member)) {
                deadMembers.add(member);
            }
//            clearExecutor.schedule(() -> clearMember(member), getSettings().getDeleteThreshold() * getSettings().getGossipInterval(), TimeUnit.MILLISECONDS);
            fireGossipEvent(member, GossipStateEnum.DOWN);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            globalLock.writeLock().unlock();
        }
    }

    private void up(GossipNode member) {
        try {
            globalLock.writeLock().lock();
            member.setState(GossipStateEnum.UP);
            if (!liveMembers.contains(member)) {
                liveMembers.add(member);
            }
            if (candidateMembers.containsKey(member)) {
                candidateMembers.remove(member);
            }
            if (deadMembers.contains(member)) {
                deadMembers.remove(member);
                log.info("up ~~");
                if (!member.equals(selfNode())) {
                    fireGossipEvent(member, GossipStateEnum.UP);
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            globalLock.writeLock().unlock();
        }

    }

    private void downing(GossipNode member, HeartbeatState state) {
        log.info("downing ~~");
        try {//11
            if (candidateMembers.containsKey(member)) {
                CandidateMemberState cState = candidateMembers.get(member);
                if (state.getHeartbeatTime() == cState.getHeartbeatTime()) {
                    cState.updateCount();
                } else if (state.getHeartbeatTime() > cState.getHeartbeatTime()) {
                    candidateMembers.remove(member);
                }
            } else {
                candidateMembers.put(member, new CandidateMemberState(state.getHeartbeatTime()));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void checkCandidate() {
        Set<GossipNode> keys = candidateMembers.keySet();
        for (GossipNode m : keys) {
            if (candidateMembers.get(m).getDowningCount().get() >= convergenceCount()) {
                down(m);
                candidateMembers.remove(m);
            }
        }
    }


    protected void shutdown() {
        messageService.unListen();
        gossipScheduleExecutor.shutdown();
        try {
            Thread.sleep(setting().getGossipInterval());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ByteBuf buffer = encodeShutdownMessage();
        for (int i = 0; i < getLiveMembers().size(); i++) {
            sendGossip(buffer, getLiveMembers(), i);
        }
        isWorking = false;
    }

    public void publish(Object payload) {
        RegularMessage msg = new RegularMessage(selfNode(), payload, convictedTime());
        messageManager.add(msg);
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public void setMessageManager(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    public GossipSettings setting() {
        return gossipSettings;
    }
}