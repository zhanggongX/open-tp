package cn.opentp.gossip.message.handler;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.node.GossipNode;
import com.alibaba.fastjson2.JSON;

/**
 * 处理集群节点下线信息
 */
public class ShutdownMessageHandler implements MessageHandler {

    @Override
    public void handle(String cluster, String data, String from) {
        GossipNode remoteNode = JSON.parseObject(data, GossipNode.class);
        if (remoteNode != null) {
            GossipApp.instance().gossipNodeContext().down(remoteNode);
        }
    }
}
