package cn.opentp.gossip.message.handler;

import cn.opentp.core.util.JSONUtils;
import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.message.Ack2Message;
import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.node.HeartbeatState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Ack2MessageHandler implements MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(Ack2MessageHandler.class);

    @Override
    public void handle(String cluster, String data, String from) {
        Ack2Message ack2Message = JSONUtils.fromJson(data, Ack2Message.class);
        Map<GossipNode, HeartbeatState> deltaGossipNodes = ack2Message.getClusterNodes();
        GossipApp.instance().gossipNodeContext().updateLocalClusterNodes(deltaGossipNodes);
    }
}
