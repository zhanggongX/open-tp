package cn.opentp.gossip.handler;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.message.Ack2Message;
import cn.opentp.gossip.model.GossipNode;
import cn.opentp.gossip.model.HeartbeatState;
import com.alibaba.fastjson2.JSON;

import java.util.Map;

/**
 * @author lvsq
 */
public class Ack2MessageHandler implements MessageHandler {

    @Override
    public void handle(String cluster, Object data, String from) {
        Ack2Message ack2Message = (Ack2Message) data;

        Map<GossipNode, HeartbeatState> deltaEndpoints = ack2Message.getEndpoints();
        GossipApp.instance().apply2LocalState(deltaEndpoints);
    }
}
