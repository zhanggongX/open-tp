package cn.opentp.gossip.message.handler;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.message.holder.GossipMessageHolder;
import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.message.GossipMessage;
import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class RegularMessageHandler implements MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(RegularMessageHandler.class);

    private static final ConcurrentHashMap<String, String> RECEIVED = new ConcurrentHashMap<>();

    @Override
    public void handle(String cluster, String data, String from) {

        GossipMessage gossipRegularMessage = JSON.parseObject(data, GossipMessage.class);

        GossipMessageHolder mm = GossipApp.instance().gossipMessageHolder();
        String creatorId = gossipRegularMessage.getCreator().getNodeId();
        if (!RECEIVED.containsKey(creatorId)) {
            RECEIVED.put(creatorId, gossipRegularMessage.getId());
        } else {
            String rcvedId = RECEIVED.get(creatorId);
            int c = gossipRegularMessage.getId().compareTo(rcvedId);
            if (c <= 0) {
                return;
            } else {
                GossipMessage remove = mm.remove(rcvedId);
                log.warn("remove: {}", remove);
                RECEIVED.put(creatorId, gossipRegularMessage.getId());
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("Received a message from : [" + from + "], message : [" + gossipRegularMessage + "]");
        }
        if (!mm.contains(gossipRegularMessage.getId())) {
            gossipRegularMessage.setForwardCount(0);
            mm.add(gossipRegularMessage);

            GossipApp.instance().listener().gossipEvent(gossipRegularMessage.getCreator(), GossipStateEnum.RECEIVE, gossipRegularMessage.getPayload());
        }
    }
}
