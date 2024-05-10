package cn.opentp.gossip.handler;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.core.GossipMessageHolder;
import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.core.GossipRegularMessage;
import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class RegularMessageHandler implements MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(RegularMessageHandler.class);

    private static final ConcurrentHashMap<String, String> RECEIVED = new ConcurrentHashMap<>();

    @Override
    public void handle(String cluster, String data, String from) {

        GossipRegularMessage msg = JSON.parseObject(data, GossipRegularMessage.class);
        GossipMessageHolder mm = GossipApp.instance().messageHolder();
        String creatorId = msg.getCreator().getNodeId();
        if (!RECEIVED.containsKey(creatorId)) {
            RECEIVED.put(creatorId, msg.getId());
        } else {
            String rcvedId = RECEIVED.get(creatorId);
            int c = msg.getId().compareTo(rcvedId);
            if (c <= 0) {
                return;
            } else {
                mm.remove(rcvedId);
                RECEIVED.put(creatorId, msg.getId());
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("Received a message from : [" + from + "], message : [" + msg + "]");
        }
        if (!mm.contains(msg.getId())) {
            msg.setForwardCount(0);
            mm.add(msg);
            GossipApp.instance().fireGossipEvent(msg.getCreator(), GossipStateEnum.RECEIVE, msg.getPayload());
        }
    }
}
