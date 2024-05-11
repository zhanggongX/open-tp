package cn.opentp.gossip;

import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.event.GossipEventTrigger;
import cn.opentp.gossip.event.GossipListener;
import cn.opentp.gossip.message.GossipMessage;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
import cn.opentp.gossip.message.holder.GossipMessageHolder;
import cn.opentp.gossip.message.holder.MemoryGossipMessageHolder;
import cn.opentp.gossip.model.*;
import cn.opentp.gossip.network.NetworkService;
import cn.opentp.gossip.network.UDPNetworkService;
import cn.opentp.gossip.schedule.GossipScheduleTask;
import cn.opentp.gossip.util.CommonUtil;
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

    // 网络服务
    private final NetworkService networkService = new UDPNetworkService();
    // 设置信息
    private final GossipSettings settings = new GossipSettings();
    // 事件监听器
    private GossipListener listener;
    // 流言消息缓存
    private final GossipMessageHolder gossipMessageHolder = new MemoryGossipMessageHolder();

    // 锁
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    // 周期定时任务执行
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

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

    public NetworkService networkService() {
        return networkService;
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

    public ScheduledExecutorService scheduledExecutorService() {
        return scheduledExecutorService;
    }

    public Map<GossipNode, CandidateMemberState> candidateMembers() {
        return candidateMembers;
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
        return settings;
    }

    public Boolean getSeedNode() {
        return seedNode;
    }

    public GossipListener listener() {
        return listener;
    }

    public void setListener(GossipListener gossipListener) {
        this.listener = gossipListener;
    }

    /**
     * 启动服务
     */
    public void startup() {
        // 启动服务监听
        networkService().start(setting().getHost(), setting().getPort());
        // 启动传播线程
        scheduledExecutorService().scheduleAtFixedRate(new GossipScheduleTask(), setting().getGossipInterval(), setting().getGossipInterval(), TimeUnit.MILLISECONDS);
        // 触发 join 事件
        GossipEventTrigger.fireGossipEvent(setting().getLocalNode(), GossipStateEnum.JOIN, null);
    }

    /**
     * 关闭服务
     */
    public void shutdown() {
        // 关闭服务监听
        networkService().close();
        // 关闭传播线程
        scheduledExecutorService().shutdown();
        // 等待消息处理完成
        try {
            Thread.sleep(setting().getGossipInterval());
        } catch (InterruptedException e) {
            log.error("服务关闭期间异常: ", e);
        }
        // 发送关闭消息
        sendShutdown();
        // 设置工作状态
        working = false;
    }

    private void sendShutdown() {
        ByteBuf byteBuf = GossipMessageCodec.codec().encodeShutdownMessage();
        final List<GossipNode> gossipNodes = liveNodes();
        for (GossipNode node : gossipNodes) {
            try {
                if (!node.equals(selfNode())) {
                    networkService().send(node.getHost(), node.getPort(), byteBuf);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * 发布流言
     */
    public void publish(Object payload) {
        GossipMessage gossipMessage = new GossipMessage(selfNode(), payload, CommonUtil.convictedTime());
        gossipMessageHolder().add(gossipMessage);
    }
}