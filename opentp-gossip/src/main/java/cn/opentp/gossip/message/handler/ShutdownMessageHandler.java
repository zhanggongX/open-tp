package cn.opentp.gossip.message.handler;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.util.GossipJacksonUtil;

/**
 * 处理集群节点下线信息
 */
public class ShutdownMessageHandler implements MessageHandler {

    @Override
    public void handle(String cluster, String data, String from) {
        GossipNode remoteNode = GossipJacksonUtil.parseJson(data, GossipNode.class);
        if (remoteNode != null) {
            GossipApp.instance().gossipNodeContext().down(remoteNode);
        }
    }
}
