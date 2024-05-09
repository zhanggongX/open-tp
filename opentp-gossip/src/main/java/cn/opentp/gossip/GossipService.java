package cn.opentp.gossip;

import cn.opentp.gossip.event.GossipListener;
import cn.opentp.gossip.model.GossipNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gossip 服务入口
 */
public class GossipService {

    private static final Logger log = LoggerFactory.getLogger(GossipService.class);

    private static final GossipManagement GOSSIP_MANAGEMENT = GossipManagement.instance();

    /**
     * 系统初始化
     *
     * @param properties 可配置入参
     */
    public static void init(GossipProperties properties) {
        GossipSettings.parseConfig(properties);
        // 标记配置完成
        GOSSIP_MANAGEMENT.initMark();
    }

    /**
     * 系统初始化
     *
     * @param properties     可配置入参
     * @param gossipListener 自定义事件处理器
     */
    public static void init(GossipProperties properties, GossipListener gossipListener) {
        GOSSIP_MANAGEMENT.setGossipListener(gossipListener);
        init(properties);
    }

    /**
     * 服务开启
     */
    public synchronized static void start() {

        if (!GOSSIP_MANAGEMENT.hadInit()) {
            log.info("Gossip 未初始化，请先执行: {}", "cn.opentp.gossip.GossipService.init()");
            return;
        }

        if (GOSSIP_MANAGEMENT.working()) {
            log.info("Gossip 请勿重复启动");
            return;
        }

        GossipNode localNode = GOSSIP_MANAGEMENT.selfNode();

        log.info("Starting {} gossip, host:{}, port:{}, nodeId:{}",
                localNode.getCluster(),
                localNode.getHost(),
                localNode.getPort(),
                localNode.getNodeId());

        // 服务器启动
        GOSSIP_MANAGEMENT.startup();
    }

    /**
     * 服务关闭
     */
    public void shutdown() {
        if (GOSSIP_MANAGEMENT.working()) {
            GOSSIP_MANAGEMENT.shutdown();
        }
    }
}
