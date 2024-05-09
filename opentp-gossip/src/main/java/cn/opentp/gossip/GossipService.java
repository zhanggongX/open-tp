package cn.opentp.gossip;

import cn.opentp.gossip.core.GossipTask;
import cn.opentp.gossip.event.GossipListener;
import cn.opentp.gossip.model.GossipNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gossip 服务入口
 */
public class GossipService {

    private static final Logger log = LoggerFactory.getLogger(GossipService.class);

    private static final GossipManager gossipApp = GossipManager.instance();

    /**
     * 系统初始化
     *
     * @param properties 可配置入参
     */
    public static void init(GossipProperties properties) {
        GossipSettings.parseConfig(properties);
        // 标记配置完成
        gossipApp.initMark();
    }

    /**
     * 系统初始化
     *
     * @param properties     可配置入参
     * @param gossipListener 事件处理器
     */
    public static void init(GossipProperties properties, GossipListener gossipListener) {
        gossipApp.setGossipListener(gossipListener);
        init(properties);
    }

    /**
     * 服务开启
     */
    public synchronized static void start() {

        if (!gossipApp.hadInit()) {
            log.info("Gossip 未初始化，请先执行: {}", "cn.opentp.gossip.GossipService.init()");
            return;
        }

        if (gossipApp.working()) {
            log.info("Gossip 已开启");
            return;
        }

        GossipNode localGossipNode = gossipApp.selfNode();

        log.info("Starting {} gossip!, host:{}  port:{} nodeId:{}", localGossipNode.getCluster(),
                localGossipNode.getHost(), localGossipNode.getPort(), localGossipNode.getNodeId());


        gossipApp.startListen();

        // 流言任务开始
        GossipTask.startup();

        gossipApp.workingMark();
    }

    /**
     * 服务关闭
     */
    public void shutdown() {
        if (gossipApp.working()) {
            GossipManager.instance().shutdown();
        }
    }
}
