package cn.opentp.gossip.message.handler;

import cn.opentp.core.util.JacksonUtil;
import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
import cn.opentp.gossip.node.GossipNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理集群节点下线信息
 */
public class ShutdownMessageHandler implements MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(ShutdownMessageHandler.class);

    @Override
    public void handle(String cluster, byte[] data, String from) {

        GossipNode remoteNode = GossipMessageCodec.codec().decodeMessage(data, GossipNode.class);
        log.trace("shutdown message: {}", JacksonUtil.toJSONString(remoteNode));

        if (remoteNode != null) {
            GossipApp.instance().gossipNodeContext().down(remoteNode);
        }
    }
}
