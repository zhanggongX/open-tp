package cn.opentp.gossip.message.handler;

import cn.opentp.core.util.JSONUtils;
import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.node.GossipNode;

/**
 * 处理集群节点下线信息
 */
public class ShutdownMessageHandler implements MessageHandler {

    @Override
    public void handle(String cluster, String data, String from) {
        GossipNode remoteNode = JSONUtils.fromJson(data, GossipNode.class);
        if (remoteNode != null) {
            GossipApp.instance().gossipNodeContext().down(remoteNode);
        }
    }
}
