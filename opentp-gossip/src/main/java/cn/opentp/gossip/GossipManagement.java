package cn.opentp.gossip;

import cn.opentp.gossip.core.*;
import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.enums.MessageTypeEnum;
import cn.opentp.gossip.event.DefaultGossipListener;
import cn.opentp.gossip.event.GossipListener;
import cn.opentp.gossip.model.*;
import cn.opentp.gossip.net.MessageService;
import cn.opentp.gossip.net.udp.UDPMessageService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Gossip 全局唯一实例
 */
public class GossipManagement {

    private static final Logger log = LoggerFactory.getLogger(GossipManagement.class);
    // 实例
    private static final GossipManagement INSTANCE = new GossipManagement();

    private final ReentrantReadWriteLock globalLock = new ReentrantReadWriteLock();
    private final ScheduledExecutorService gossipScheduleExecutor = Executors.newScheduledThreadPool(1);

    private final MessageService messageService = new UDPMessageService();

    private boolean hadInit = false;
    private boolean working = false;
    private Boolean seedNode = null;

    private final Map<GossipNode, HeartbeatState> endpointMembers = new ConcurrentHashMap<>();
    private final List<GossipNode> liveMembers = new CopyOnWriteArrayList<>();
    private final List<GossipNode> deadMembers = new CopyOnWriteArrayList<>();
    private final Map<GossipNode, CandidateMemberState> candidateMembers = new ConcurrentHashMap<>();

    private GossipSettings gossipSettings = new GossipSettings();
    private GossipListener listener = new DefaultGossipListener();

    private final Random random = new Random();

    private MessageManager messageManager = new InMemMessageManager();


    private GossipManagement() {
    }

    public static GossipManagement instance() {
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

    public List<GossipNode> liveMembers() {
        return liveMembers;
    }

    public List<GossipNode> deadMembers() {
        return deadMembers;
    }

    public boolean working() {
        return working;
    }

    public Map<GossipNode, HeartbeatState> endpointMembers() {
        return endpointMembers;
    }

    public ReentrantReadWriteLock globalLock() {
        return globalLock;
    }

    public ScheduledExecutorService gossipScheduleExecutor() {
        return gossipScheduleExecutor;
    }

    public Map<GossipNode, CandidateMemberState> candidateMembers() {
        return candidateMembers;
    }

    public Random getRandom() {
        return random;
    }

    public void setGossipListener(GossipListener gossipListener) {
        listener = gossipListener;
    }

    public void netStartup() {
        messageService.listen(setting().getHost(), setting().getPort());
    }

    public void initMark() {
        hadInit = true;
    }

    public void workingMark() {
        working = true;
    }

    public boolean hadInit() {
        return hadInit;
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

    private SeedNode gossipMember2SeedMember(GossipNode member) {
        return new SeedNode(member.getCluster(), member.getNodeId(), member.getHost(), member.getPort());
    }

    private int convergenceCount() {
        int size = endpointMembers().size();
        return (int) Math.floor(Math.log10(size) + Math.log(size) + 1);
    }

    private long convictedTime() {
        long executeGossipTime = 500;
        return ((convergenceCount() * (setting().getNetworkDelay() * 3L + executeGossipTime)) << 1) + setting().getGossipInterval();
    }

    public Boolean getSeedNode() {
        return seedNode;
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


    protected void shutdown() {
        messageService.unListen();
        gossipScheduleExecutor.shutdown();
        try {
            Thread.sleep(setting().getGossipInterval());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ByteBuf buffer = encodeShutdownMessage();
        for (int i = 0; i < liveMembers().size(); i++) {
            sendGossip(buffer, liveMembers(), i);
        }
        working = false;
    }

    public void publish(Object payload) {
        RegularMessage msg = new RegularMessage(selfNode(), payload, convictedTime());
        messageManager.add(msg);
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public GossipSettings setting() {
        return gossipSettings;
    }

    public void gossipStartup() {
        gossipScheduleExecutor.scheduleAtFixedRate(new GossipTask(), setting().getGossipInterval(), setting().getGossipInterval(), TimeUnit.MILLISECONDS);
    }
}