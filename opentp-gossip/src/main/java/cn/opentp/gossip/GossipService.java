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

    private static final GossipManagement gossipManagement = GossipManagement.instance();

    /**
     * 系统初始化
     *
     * @param properties 可配置入参
     */
    public static void init(GossipProperties properties) {
        GossipSettings.parseConfig(properties);
        // 标记配置完成
        gossipManagement.initMark();
    }

    /**
     * 系统初始化
     *
     * @param properties     可配置入参
     * @param gossipListener 自定义事件处理器
     */
    public static void init(GossipProperties properties, GossipListener gossipListener) {
        gossipManagement.setGossipListener(gossipListener);
        init(properties);
    }

    /**
     * 服务开启
     */
    public synchronized static void start() {

        if (!gossipManagement.hadInit()) {
            log.info("Gossip 未初始化，请先执行: {}", "cn.opentp.gossip.GossipService.init()");
            return;
        }

        if (gossipManagement.working()) {
            log.info("Gossip 请勿重复启动");
            return;
        }

        GossipNode localGossipNode = gossipManagement.selfNode();

        log.info("Starting {} gossip!, host:{}  port:{} nodeId:{}",
                localGossipNode.getCluster(), localGossipNode.getHost(), localGossipNode.getPort(), localGossipNode.getNodeId());

        // 启动网络服务
        gossipManagement.netStartup();

        // 流言任务
        gossipManagement.gossipStartup();

        // 运行标记
        gossipManagement.workingMark();
    }

    /**
     * 服务关闭
     */
    public void shutdown() {
        if (gossipManagement.working()) {
            gossipManagement.shutdown();
        }
    }
}
