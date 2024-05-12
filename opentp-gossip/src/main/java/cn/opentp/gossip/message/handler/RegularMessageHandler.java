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

        GossipMessage gossipMessage = JSON.parseObject(data, GossipMessage.class);

        GossipMessageHolder mm = GossipApp.instance().gossipMessageHolder();
        String publishNodeId = gossipMessage.getPublishNode().getNodeId();
        if (!RECEIVED.containsKey(publishNodeId)) {
            RECEIVED.put(publishNodeId, gossipMessage.getMessageId());
        } else {
            String rcvedId = RECEIVED.get(publishNodeId);
            int c = gossipMessage.getMessageId().compareTo(rcvedId);
            if (c <= 0) {
                return;
            } else {
                GossipMessage remove = mm.remove(rcvedId);
                log.warn("remove: {}", remove);
                RECEIVED.put(publishNodeId, gossipMessage.getMessageId());
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("Received a message from : [" + from + "], message : [" + gossipMessage + "]");
        }
        if (!mm.contains(gossipMessage.getMessageId())) {
            gossipMessage.setForwardCount(0);
            mm.add(gossipMessage);

            GossipApp.instance().gossipListenerContext().fireGossipEvent(gossipMessage.getPublishNode(), GossipStateEnum.RECEIVE, gossipMessage.getPayload());
        }
    }
}
