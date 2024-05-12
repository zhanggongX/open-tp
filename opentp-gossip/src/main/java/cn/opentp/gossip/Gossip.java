package cn.opentp.gossip;

import cn.opentp.gossip.event.DefaultGossipListener;
import cn.opentp.gossip.event.GossipListener;
import cn.opentp.gossip.node.GossipNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gossip 服务入口
 */
public class Gossip {

    private static final Logger log = LoggerFactory.getLogger(Gossip.class);

    private static final GossipApp gossipApp = GossipApp.instance();

    /**
     * 系统初始化
     *
     * @param properties 可配置入参
     */
    public static void init(GossipProperties properties) {
        init(properties, new DefaultGossipListener());
    }

    /**
     * 系统初始化
     *
     * @param properties     可配置入参
     * @param gossipListener 自定义事件处理器，处理在集群中广播的信息
     */
    public static void init(GossipProperties properties, GossipListener gossipListener) {
        GossipSettings.parseConfig(properties);
        gossipApp.gossipListenerContext().setGossipListener(gossipListener);
        // 标记配置完成
        gossipApp.initMark();
    }

    /**
     * 服务开启
     */
    public synchronized static void start() {

        if (!gossipApp.hadInit()) {
            log.info("Gossip 未初始化，请先执行: {}", "cn.opentp.gossip.GossipService.init()");
            System.exit(-1);
        }

        if (gossipApp.working()) {
            log.info("Gossip 请勿重复启动");
            System.exit(-1);
        }

        GossipNode localNode = gossipApp.selfNode();
        log.info("Starting {} gossip, host:{}, port:{}, nodeId:{}",
                localNode.getCluster(),
                localNode.getHost(),
                localNode.getPort(),
                localNode.getNodeId());

        // 服务器启动
        gossipApp.startup();
        gossipApp.workingMark();
    }

    /**
     * 服务关闭
     */
    public void shutdown() {
        if (gossipApp.working()) {
            gossipApp.shutdown();
        }
    }
}
