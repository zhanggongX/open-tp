package cn.opentp.gossip.message.handler;

import cn.opentp.core.util.JacksonUtil;
import cn.opentp.gossip.GossipEnvironment;
import cn.opentp.gossip.message.Ack2Message;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.node.HeartbeatState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Ack2MessageHandler implements MessageHandler {

    private final static Logger log = LoggerFactory.getLogger(Ack2MessageHandler.class);

    @Override
    public void handle(String cluster, byte[] data, String from) {

        Ack2Message ack2Message = GossipMessageCodec.codec().decodeMessage(data, Ack2Message.class);
        log.trace("ack2 message: {}", JacksonUtil.toJSONString(ack2Message));
        
        Map<GossipNode, HeartbeatState> deltaGossipNodes = ack2Message.getClusterNodes();
        GossipEnvironment.instance().gossipNodeContext().updateLocalClusterNodes(deltaGossipNodes);
    }
}
