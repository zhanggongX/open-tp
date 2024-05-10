package cn.opentp.gossip.handler;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.model.GossipNode;
import com.alibaba.fastjson2.JSON;

public class ShutdownMessageHandler implements MessageHandler {

    @Override
    public void handle(String cluster, Object data, String from) {

        GossipNode whoShutdown = (GossipNode) data;

        if (whoShutdown != null) {
            GossipApp.instance().down(whoShutdown);
        }
    }
}
