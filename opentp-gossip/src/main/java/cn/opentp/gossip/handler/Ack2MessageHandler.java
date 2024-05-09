package cn.opentp.gossip.handler;

import cn.opentp.gossip.GossipManagement;
import cn.opentp.gossip.model.Ack2Message;
import cn.opentp.gossip.model.GossipNode;
import cn.opentp.gossip.model.HeartbeatState;
import com.alibaba.fastjson2.JSON;

import java.util.Map;

/**
 * @author lvsq
 */
public class Ack2MessageHandler implements MessageHandler {
    @Override
    public void handle(String cluster, String data, String from) {

        Ack2Message ack2Message = JSON.parseObject(data, Ack2Message.class);

        Map<GossipNode, HeartbeatState> deltaEndpoints = ack2Message.getEndpoints();
        GossipManagement.instance().apply2LocalState(deltaEndpoints);
    }
}
