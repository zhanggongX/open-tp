package cn.opentp.gossip.message.handler;

import cn.opentp.gossip.model.GossipNode;
import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownMessageHandler extends AbstractMessageHandler implements MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(ShutdownMessageHandler.class);

    @Override
    public void handle(String cluster, String data, String from) {

        GossipNode gossipNode = JSON.parseObject(data, GossipNode.class);
        log.info("");
        if (gossipNode != null) {
            down(gossipNode);
        }
    }
}
