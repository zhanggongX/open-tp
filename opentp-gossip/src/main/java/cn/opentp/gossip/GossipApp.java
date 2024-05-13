package cn.opentp.gossip;

import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.event.GossipListenerContext;
import cn.opentp.gossip.message.GossipMessage;
import cn.opentp.gossip.message.holder.GossipMessageHolder;
import cn.opentp.gossip.message.holder.MemoryGossipMessageHolder;
import cn.opentp.gossip.network.NetworkService;
import cn.opentp.gossip.network.UDPNetworkService;
import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.node.GossipNodeContext;
import cn.opentp.gossip.schedule.GossipScheduleTask;
import cn.opentp.gossip.util.GossipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Gossip 全局唯一实例
 */
public class GossipApp {

    private static final Logger log = LoggerFactory.getLogger(GossipApp.class);
    // 实例
    private static final GossipApp INSTANCE = new GossipApp();

    // 初始化标记
    private volatile boolean hadInit = false;
    // 开启运行标记
    private volatile boolean working = false;
    // 是否发送节点标记
    private Boolean seedNode = null;

    // 网络服务
    private final NetworkService networkService = new UDPNetworkService();
    // 设置信息
    private final GossipSettings settings = new GossipSettings();
    // 事件监听器
    private final GossipListenerContext gossipListenerContext = new GossipListenerContext();
    // 流言消息缓存
    private final GossipMessageHolder gossipMessageHolder = new MemoryGossipMessageHolder();

    // 锁
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    // 周期定时任务执行
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    // Gossip 节点上下文
    private final GossipNodeContext gossipNodeContext = new GossipNodeContext();

    private GossipApp() {
    }

    public static GossipApp instance() {
        return INSTANCE;
    }

    public NetworkService networkService() {
        return networkService;
    }

    public boolean working() {
        return working;
    }

    public ReentrantReadWriteLock lock() {
        return lock;
    }

    public ScheduledExecutorService scheduledExecutorService() {
        return scheduledExecutorService;
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

    public GossipListenerContext gossipListenerContext() {
        return gossipListenerContext;
    }

    public GossipNodeContext gossipNodeContext() {
        return gossipNodeContext;
    }

    /**
     * 启动服务
     */
    public synchronized void startup() {
        if (!hadInit()) {
            log.info("Gossip 未初始化，请先执行: {}", "cn.opentp.gossip.GossipService.init()");
            System.exit(-1);
        }

        // 启动服务监听
        networkService().start(setting().getHost(), setting().getPort());
        // 启动传播线程
        scheduledExecutorService().scheduleAtFixedRate(new GossipScheduleTask(), setting().getGossipInterval(), setting().getGossipInterval(), TimeUnit.MILLISECONDS);
        // 触发 join 事件
        gossipListenerContext.fireGossipEvent(setting().getLocalNode(), GossipStateEnum.JOIN, null);
    }

    /**
     * 关闭服务
     */
    public synchronized void shutdown() {
        if (!working()) {
            log.error("Gossip 未启动，关闭服务退出");
            return;
        }

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
        gossipNodeContext().selfNodeShutdown();
        // 设置工作状态
        working = false;
    }

    /**
     * 发布流言
     */
    public void publish(Object payload) {
        if (!working()) {
            log.error("Gossip 未启动，发布流言失败！");
            return;
        }

        GossipMessage gossipMessage = new GossipMessage(selfNode(), payload, GossipUtil.convictedTime());
        gossipMessageHolder().add(gossipMessage);
    }
}