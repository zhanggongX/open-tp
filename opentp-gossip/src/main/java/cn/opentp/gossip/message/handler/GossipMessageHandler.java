package cn.opentp.gossip.message.handler;

import cn.opentp.core.util.JacksonUtil;
import cn.opentp.gossip.GossipEnvironment;
import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.message.GossipMessage;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
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
    public void handle(String cluster, byte[] data, String from) {
        GossipMessageHolder messageHolder = GossipEnvironment.instance().gossipMessageHolder();

        GossipMessage gossipMessage = GossipMessageCodec.codec().decodeMessage(data, GossipMessage.class);
        log.trace("gossip message: {}", JacksonUtil.toJSONString(gossipMessage));
        
        // 流言发布节点ID
        String publishNodeId = gossipMessage.getPublishNode().getNodeId();
        if (!RECEIVED_MSG.containsKey(publishNodeId)) {
            // 当前节点没有该节点的流言记录，直接放入。
            RECEIVED_MSG.put(publishNodeId, gossipMessage.getMessageId());
        } else {
            // 取出该节点的流言ID，该ID随时间递增。
            String receivedMessageId = RECEIVED_MSG.get(publishNodeId);
            int compareRes = gossipMessage.getMessageId().compareTo(receivedMessageId);
            if (compareRes <= 0) {
                // 老消息
                return;
            } else {
                // 新消息
                GossipMessage removeMessage = messageHolder.remove(receivedMessageId);
                log.trace("remove: {}", removeMessage);
                RECEIVED_MSG.put(publishNodeId, gossipMessage.getMessageId());
            }
        }

        log.trace("received a message from: {}, message: {}", from, gossipMessage);
        // 放入流言容器，随后被扩散出去。
        if (!messageHolder.contains(gossipMessage.getMessageId())) {
            gossipMessage.setForwardCount(0);
            messageHolder.add(gossipMessage);
            // 触发事件，用户自定义处理该事件。
            GossipEnvironment.instance().gossipListenerContext().fireGossipEvent(gossipMessage.getPublishNode(), GossipStateEnum.RECEIVE, gossipMessage.getPayload());
        }
    }
}
