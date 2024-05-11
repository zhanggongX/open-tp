package cn.opentp.gossip;

import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.event.DefaultGossipListener;
import cn.opentp.gossip.event.GossipListener;
import cn.opentp.gossip.message.GossipMessage;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
import cn.opentp.gossip.message.holder.GossipMessageHolder;
import cn.opentp.gossip.message.holder.MemoryGossipMessageHolder;
import cn.opentp.gossip.model.*;
import cn.opentp.gossip.message.service.MessageService;
import cn.opentp.gossip.message.service.UDPMessageService;
import cn.opentp.gossip.schedule.GossipScheduleTask;
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

    // 初始化标记
    private boolean hadInit = false;
    // 开启运行标记
    private boolean working = false;
    // 是否发送节点标记
    private Boolean seedNode = null;

    // 消息服务
    private final MessageService messageService = new UDPMessageService();
    // 设置信息
    private final GossipSettings gossipSettings = new GossipSettings();
    // 事件监听器
    private GossipListener gossipListener = new DefaultGossipListener();
    // 流言消息缓存
    private final GossipMessageHolder gossipMessageHolder = new MemoryGossipMessageHolder();

    // 锁
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    // 周期定时任务执行
    private final ScheduledExecutorService gossipScheduleExecutor = Executors.newScheduledThreadPool(1);

    // 节点心跳信息
    private final Map<GossipNode, HeartbeatState> endpointNodeCache = new ConcurrentHashMap<>();
    // 活节点
    private final List<GossipNode> liveNodes = new CopyOnWriteArrayList<>();
    // 死节点
    private final List<GossipNode> deadNodes = new CopyOnWriteArrayList<>();
    // 候选节点，判定中的节点
    private final Map<GossipNode, CandidateMemberState> candidateMembers = new ConcurrentHashMap<>();

    private GossipApp() {
    }

    public static GossipApp instance() {
        return INSTANCE;
    }

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

    public ReentrantReadWriteLock lock() {
        return lock;
    }

    public ScheduledExecutorService gossipScheduleExecutor() {
        return gossipScheduleExecutor;
    }

    public Map<GossipNode, CandidateMemberState> candidateMembers() {
        return candidateMembers;
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

    public GossipMessageHolder gossipMessageHolder() {
        return gossipMessageHolder;
    }

    public GossipSettings setting() {
        return gossipSettings;
    }

    public Boolean getSeedNode() {
        return seedNode;
    }

    public GossipListener gossipListener() {
        return gossipListener;
    }

    public void setGossipListener(GossipListener gossipListener) {
        this.gossipListener = gossipListener;
    }

    public void fireGossipEvent(GossipNode member, GossipStateEnum state) {
        fireGossipEvent(member, state, null);
    }

    public void fireGossipEvent(GossipNode member, GossipStateEnum state, Object payload) {
        if (gossipListener() != null) {
            if (state == GossipStateEnum.RECEIVE) {
                new Thread(() -> gossipListener().gossipEvent(member, state, payload)).start();
            } else {
                gossipListener().gossipEvent(member, state, payload);
            }
        }
    }


    /**
     * 启动服务
     */
    public void startup() {
        netStartup();
        gossipStartup();
        fireGossipEvent(setting().getLocalNode(), GossipStateEnum.JOIN);
    }

    /**
     * 启动流言发送线程
     */
    public void gossipStartup() {
        gossipScheduleExecutor.scheduleAtFixedRate(new GossipScheduleTask(), setting().getGossipInterval(), setting().getGossipInterval(), TimeUnit.MILLISECONDS);
    }

    /**
     * 关闭服务
     */
    public void shutdown() {
        messageService.close();
        gossipScheduleExecutor.shutdown();
        // 等待消息发送完成
        try {
            Thread.sleep(setting().getGossipInterval());
        } catch (InterruptedException e) {
            log.error("服务关闭期间异常: ", e);
        }
        sendShutdown();
        working = false;
    }

    private void sendShutdown() {
        ByteBuf byteBuf = GossipMessageCodec.codec().encodeShutdownMessage();
        final List<GossipNode> gossipNodes = liveNodes();
        for (int i = 0; i < gossipNodes.size(); i++) {
            try {
                GossipNode gossipNode = gossipNodes.get(i);
                if (!gossipNode.equals(selfNode())) {
                    messageService.send(gossipNode.getHost(), gossipNode.getPort(), byteBuf);
                }
                // setting().getSendNodes().contains(gossipMember2SeedMember(gossipNode));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * 发布流言
     */
    public void publish(Object payload) {
        GossipMessage msg = new GossipMessage(selfNode(), payload, convictedTime());
        gossipMessageHolder.add(msg);
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
}