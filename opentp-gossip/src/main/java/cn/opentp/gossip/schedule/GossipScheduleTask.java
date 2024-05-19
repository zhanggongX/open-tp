package cn.opentp.gossip.schedule;

import cn.opentp.gossip.GossipEnvironment;
import cn.opentp.gossip.message.SyncMessage;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.node.GossipNodeContext;
import cn.opentp.gossip.node.GossipNodeDigest;
import cn.opentp.gossip.node.HeartbeatState;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * 流言传播 Task
 * 该 Task 不宜过快
 * 因为同步消息出去需要时间，如果同步过快，则可能导致发送消息任务堆积。
 */
public class GossipScheduleTask extends AbstractGossipTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(GossipScheduleTask.class);

    @Override
    public void run() {
        GossipEnvironment environment = GossipEnvironment.instance();
        GossipNodeContext nodeContext = environment.gossipNodeContext();

        //Update local member version
        Map<GossipNode, HeartbeatState> endpointMembers = nodeContext.clusterNodes();
        HeartbeatState heartbeatState = endpointMembers.get(environment.selfNode());
        long version = heartbeatState.updateVersion();
        log.trace("heartbeat version is {}", version);

        // 如果当前节点处于待加入集群状态，执行上线
        GossipNode selfNode = environment.selfNode();
        if (nodeContext.discoverable(selfNode)) {
            nodeContext.up(selfNode);
        }

        try {
            // 获取当前所有节点的摘要信息，并同步出去
            List<GossipNodeDigest> nodeDigests = nodeContext.randomGossipNodeDigest();
            if (!nodeDigests.isEmpty()) {
                ByteBuf byteBuf = GossipMessageCodec.codec().encodeSyncMessage(new SyncMessage(environment.setting().getCluster(), nodeDigests));
                sendBuf(byteBuf);
            }
        } catch (UnknownHostException e) {
            log.error("获取节点摘要异常：", e);
        }

        nodeContext.checkStatus();

        log.trace("live nodes : {}", nodeContext.liveNodes());
        log.trace("downed nodes : {}", nodeContext.downedNodes());
        log.trace("cluster nodes : {}", nodeContext.clusterNodes());

        // 发布流言消息
        GossipEnvironment.instance().gossipExecutorService().submit(new GossipMessagePublishTask());
    }
}