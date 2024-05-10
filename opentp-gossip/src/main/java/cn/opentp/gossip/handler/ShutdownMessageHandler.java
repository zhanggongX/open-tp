package cn.opentp.gossip.handler;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.model.GossipNode;
import com.alibaba.fastjson2.JSON;

public class ShutdownMessageHandler implements MessageHandler {

    @Override
    public void handle(String cluster, String data, String from) {

        GossipNode gossipNode = JSON.parseObject(data, GossipNode.class);

        if (gossipNode != null) {
            GossipApp.instance().down(gossipNode);
        }
    }
}
