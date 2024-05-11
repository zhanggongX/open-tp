package cn.opentp.gossip.node;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.event.GossipEventTrigger;
import cn.opentp.gossip.message.GossipMessage;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
import cn.opentp.gossip.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GossipNodeContext {

    private static final Logger log = LoggerFactory.getLogger(GossipNodeContext.class);

    // 节点心跳信息
    private final Map<GossipNode, HeartbeatState> endpointNodes = new ConcurrentHashMap<>();
    // 活节点
    private final List<GossipNode> liveNodes = new CopyOnWriteArrayList<>();
    // 死节点
    private final List<GossipNode> deadNodes = new CopyOnWriteArrayList<>();
    // 候选节点，判定中的节点
    private final Map<GossipNode, CandidateMemberState> candidateMembers = new ConcurrentHashMap<>();

    public Map<GossipNode, HeartbeatState> endpointNodes() {
        return endpointNodes;
    }

    public List<GossipNode> liveNodes() {
        return liveNodes;
    }

    public List<GossipNode> deadNodes() {
        return deadNodes;
    }

    public Map<GossipNode, CandidateMemberState> candidateMembers() {
        return candidateMembers;
    }

    /**
     * 节点上线
     *
     * @param node 上线节点
     */
    public void up(GossipNode node) {
        ReentrantReadWriteLock.WriteLock writeLock = GossipApp.instance().lock().writeLock();
        try {
            writeLock.lock();
            node.setState(GossipStateEnum.UP);
            // 非原子操作
            if (!liveNodes().contains(node)) {
                liveNodes().add(node);
            }
            // 候选节点移除
            candidateMembers().remove(node);
            // 终止节点移除
            deadNodes().remove(node);

            log.debug("节点: {} 上线。", node);
            if (!node.equals(GossipApp.instance().selfNode())) {
                // 触发上线事件
                GossipEventTrigger.fireGossipEvent(node, GossipStateEnum.UP);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 节点下线
     *
     * @param node 下线节点
     */
    public void down(GossipNode node) {
        ReentrantReadWriteLock.WriteLock writeLock = GossipApp.instance().lock().writeLock();
        try {
            writeLock.lock();
            node.setState(GossipStateEnum.DOWN);
            // 活动节点移除
            liveNodes().remove(node);
            // 加入终止节点
            if (!deadNodes().contains(node)) {
                deadNodes().add(node);
            }
            // 触发下线事件
            GossipEventTrigger.fireGossipEvent(node, GossipStateEnum.DOWN);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 本节点下线，发送下线信息
     */
    public void selfNodeShutdown() {
        GossipApp gossipApp = GossipApp.instance();
        GossipNode selfNode = gossipApp.selfNode();
        ByteBuf byteBuf = GossipMessageCodec.codec().encodeShutdownMessage(selfNode);
        for (GossipNode node : liveNodes()) {
            try {
                if (!node.equals(selfNode)) {
                    gossipApp.networkService().send(node.getHost(), node.getPort(), byteBuf);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        byteBuf.release();
    }
}
