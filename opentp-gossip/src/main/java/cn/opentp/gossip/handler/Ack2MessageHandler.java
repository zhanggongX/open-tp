package cn.opentp.gossip.handler;

import cn.opentp.gossip.core.GossipManager;
import cn.opentp.gossip.model.Ack2Message;
import cn.opentp.gossip.model.GossipMember;
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

        Map<GossipMember, HeartbeatState> deltaEndpoints = ack2Message.getEndpoints();
        GossipManager.getInstance().apply2LocalState(deltaEndpoints);
    }
}
