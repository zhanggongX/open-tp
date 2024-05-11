package cn.opentp.gossip;

import cn.opentp.gossip.core.*;
import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.event.DefaultGossipListener;
import cn.opentp.gossip.event.GossipListener;
import cn.opentp.gossip.message.GossipMessage;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
import cn.opentp.gossip.message.holder.GossipMessageContext;
import cn.opentp.gossip.message.holder.MemoryGossipMessageContext;
import cn.opentp.gossip.model.*;
import cn.opentp.gossip.message.service.MessageService;
import cn.opentp.gossip.message.service.UDPMessageService;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Gossip 全局唯一实例
 */
public class GossipApp {

    private static final Logger log = LoggerFactory.getLogger(GossipApp.class);
    // 实例
    private static final GossipApp INSTANCE = new GossipApp();

    private final ReentrantReadWriteLock globalLock = new ReentrantReadWriteLock();
    private final ScheduledExecutorService gossipScheduleExecutor = Executors.newScheduledThreadPool(1);

    private final MessageService messageService = new UDPMessageService();

    private boolean hadInit = false;
    private boolean working = false;
    private Boolean seedNode = null;

    private final Map<GossipNode, HeartbeatState> endpointNodeCache = new ConcurrentHashMap<>();
    private final List<GossipNode> liveNodes = new CopyOnWriteArrayList<>();
    private final List<GossipNode> deadNodes = new CopyOnWriteArrayList<>();
    private final Map<GossipNode, CandidateMemberState> candidateMembers = new ConcurrentHashMap<>();

    private GossipSettings gossipSettings = new GossipSettings();
    private GossipListener listener = new DefaultGossipListener();

    private final Random random = new Random();

    private GossipMessageContext gossipMessageContext = new MemoryGossipMessageContext();

    private GossipApp() {
    }

    public static GossipApp instance() {
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

    public List<GossipNode> liveNodes() {
        return liveNodes;
    }

    public List<GossipNode> deadNodes() {
        return deadNodes;
    }

    public boolean working() {
        return working;
    }

    public Map<GossipNode, HeartbeatState> endpointNodeCache() {
        return endpointNodeCache;
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
        messageService.start(setting().getHost(), setting().getPort());
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

    public GossipNode selfNode() {
        return setting().getLocalNode();
    }

    public void apply2LocalState(Map<GossipNode, HeartbeatState> endpointMembers) {
        Set<GossipNode> keys = endpointMembers.keySet();
        for (GossipNode m : keys) {
            if (selfNode().equals(m)) {
                continue;
            }

            try {
                HeartbeatState localState = endpointNodeCache().get(m);
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
        if (endpointNodeCache.containsKey(member)) {
            endpointNodeCache.remove(member);
        }
        endpointNodeCache.put(member, remoteState);
    }

    public GossipNode createByDigest(GossipDigest digest) {
        GossipNode member = new GossipNode();
        member.setPort(digest.getEndpoint().getPort());
        member.setHost(digest.getEndpoint().getAddress().getHostAddress());
        member.setCluster(setting().getCluster());

        Set<GossipNode> keys = endpointNodeCache().keySet();
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
                messageService.send(target.getHost(), target.getPort(), buffer);
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
            if (state == GossipStateEnum.RECEIVE) {
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
            liveNodes.remove(member);
            if (!deadNodes.contains(member)) {
                deadNodes.add(member);
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
            if (!liveNodes.contains(member)) {
                liveNodes.add(member);
            }
            if (candidateMembers.containsKey(member)) {
                candidateMembers.remove(member);
            }
            if (deadNodes.contains(member)) {
                deadNodes.remove(member);
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


    /**
     * 启动服务
     */
    public void startup() {
        fireGossipEvent(setting().getLocalNode(), GossipStateEnum.JOIN);
        netStartup();
        gossipStartup();
        working = true;
    }

    /**
     * 启动流言发送线程
     */
    public void gossipStartup() {
        gossipScheduleExecutor.scheduleAtFixedRate(new GossipTask(), setting().getGossipInterval(), setting().getGossipInterval(), TimeUnit.MILLISECONDS);
    }

    /**
     * 关闭服务
     */
    public void shutdown() {
        messageService.close();
        gossipScheduleExecutor.shutdown();
        try {
            Thread.sleep(setting().getGossipInterval());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ByteBuf byteBuf = GossipMessageCodec.codec().encodeShutdownMessage();
        for (int i = 0; i < liveNodes().size(); i++) {
            sendGossip(byteBuf, liveNodes(), i);
        }
        working = false;
    }

    /**
     * 发布流言
     */
    public void publish(Object payload) {
        GossipMessage msg = new GossipMessage(selfNode(), payload, convictedTime());
        gossipMessageContext.add(msg);
    }

    /**
     * 判定过期时间
     */
    private long convictedTime() {
        long executeGossipTime = 500;
        return ((convergenceCount() * (gossipSettings.getNetworkDelay() * 3L + executeGossipTime)) << 1) + setting().getGossipInterval();
    }

    /**
     * 判定获取次数
     */
    private int convergenceCount() {
        int size = endpointNodeCache().size();
        return (int) Math.floor(Math.log10(size) + Math.log(size) + 1);
    }


    public GossipMessageContext gossipMessageContext() {
        return gossipMessageContext;
    }

    public GossipSettings setting() {
        return gossipSettings;
    }
}