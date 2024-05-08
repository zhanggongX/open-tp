package cn.opentp.gossip.handler;

import cn.opentp.gossip.core.GossipManager;
import cn.opentp.gossip.core.MessageManager;
import cn.opentp.gossip.model.GossipState;
import cn.opentp.gossip.model.RegularMessage;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class RegularMessageHandler implements MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(RegularMessageHandler.class);

    private static final ConcurrentHashMap<String, String> RECEIVED = new ConcurrentHashMap<>();

    @Override
    public void handle(String cluster, String data, String from) {

        RegularMessage msg = JSON.parseObject(data, RegularMessage.class);
        MessageManager mm = GossipManager.getInstance().getSettings().getMessageManager();
        String creatorId = msg.getCreator().getId();
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
            GossipManager.getInstance().fireGossipEvent(msg.getCreator(), GossipState.RCV, msg.getPayload());
        }
    }
}
