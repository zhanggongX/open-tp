package cn.opentp.gossip.message.handler;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.message.Ack2Message;
import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.node.HeartbeatState;
import cn.opentp.gossip.util.GossipJacksonUtil;

import java.util.Map;

public class Ack2MessageHandler implements MessageHandler {

    @Override
    public void handle(String cluster, String data, String from) {

        Ack2Message ack2Message = GossipJacksonUtil.parseJson(data, Ack2Message.class);
        Map<GossipNode, HeartbeatState> deltaGossipNodes = ack2Message.getClusterNodes();

        GossipApp.instance().gossipNodeContext().updateLocalClusterNodes(deltaGossipNodes);
    }
}
