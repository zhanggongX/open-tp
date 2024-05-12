package cn.opentp.gossip.message.handler;

import cn.opentp.gossip.message.Ack2Message;
import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.node.HeartbeatState;
import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Ack2MessageHandler extends AbstractMessageHandler implements MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(Ack2MessageHandler.class);

    @Override
    public void handle(String cluster, String data, String from) {

        Ack2Message ack2Message = JSON.parseObject(data, Ack2Message.class);

        Map<GossipNode, HeartbeatState> deltaEndpoints = ack2Message.getEndpoints();
        apply2LocalState(deltaEndpoints);
    }
}
