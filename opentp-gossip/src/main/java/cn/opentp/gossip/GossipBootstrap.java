package cn.opentp.gossip;

import cn.opentp.gossip.event.DefaultGossipListener;
import cn.opentp.gossip.event.GossipListener;
import cn.opentp.gossip.node.GossipNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gossip 服务入口
 */
public class GossipBootstrap {

    private static final Logger log = LoggerFactory.getLogger(GossipBootstrap.class);

    private static final GossipEnvironment environment = GossipEnvironment.instance();

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
        environment.gossipListenerContext().setGossipListener(gossipListener);
        // 标记配置完成
        environment.initMark();
    }

    /**
     * 服务开启
     */
    public synchronized static void start() {

        if (environment.working()) {
            log.error("Gossip 请勿重复启动");
            System.exit(-1);
        }

        GossipNode localNode = environment.selfNode();
        log.info("Starting {} gossip, host:{}, port:{}, nodeId:{}",
                localNode.getCluster(),
                localNode.getHost(),
                localNode.getPort(),
                localNode.getNodeId());

        // 服务器启动
        environment.startup();
        environment.workingMark();
    }

    /**
     * 服务关闭
     */
    public void shutdown() {
        environment.shutdown();
    }
}
