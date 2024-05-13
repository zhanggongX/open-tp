package cn.opentp.gossip.message.handler;

import cn.opentp.core.util.JSONUtils;
import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.message.GossipMessage;
import cn.opentp.gossip.message.holder.GossipMessageHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理集群适用者发布的流言信息
 */
public class GossipMessageHandler implements MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(GossipMessageHandler.class);

    private static final ConcurrentHashMap<String, String> RECEIVED_MSG = new ConcurrentHashMap<>();

    @Override
    public void handle(String cluster, String data, String from) {
        GossipMessage gossipMessage = JSONUtils.fromJson(data, GossipMessage.class);
        GossipMessageHolder messageHolder = GossipApp.instance().gossipMessageHolder();

        String publishNodeId = gossipMessage.getPublishNode().getNodeId();
        if (!RECEIVED_MSG.containsKey(publishNodeId)) {
            RECEIVED_MSG.put(publishNodeId, gossipMessage.getMessageId());
        } else {
            String receivedMessageId = RECEIVED_MSG.get(publishNodeId);
            int compareRes = gossipMessage.getMessageId().compareTo(receivedMessageId);
            if (compareRes <= 0) {
                // 老消息
                return;
            } else {
                // 新消息
                GossipMessage removeMessage = messageHolder.remove(receivedMessageId);
                log.debug("remove: {}", removeMessage);
                RECEIVED_MSG.put(publishNodeId, gossipMessage.getMessageId());
            }
        }

        log.trace("received a message from: {}, message: {}", from, gossipMessage);
        if (!messageHolder.contains(gossipMessage.getMessageId())) {
            gossipMessage.setForwardCount(0);
            messageHolder.add(gossipMessage);
            GossipApp.instance().gossipListenerContext().fireGossipEvent(gossipMessage.getPublishNode(), GossipStateEnum.RECEIVE, gossipMessage.getPayload());
        }
    }
}
