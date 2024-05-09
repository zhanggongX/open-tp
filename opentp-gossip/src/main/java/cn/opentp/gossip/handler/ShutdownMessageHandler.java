package cn.opentp.gossip.handler;

import cn.opentp.gossip.GossipManagement;
import cn.opentp.gossip.model.GossipNode;
import com.alibaba.fastjson2.JSON;

public class ShutdownMessageHandler implements MessageHandler {

    @Override
    public void handle(String cluster, String data, String from) {
        GossipNode whoShutdown = JSON.parseObject(data, GossipNode.class);
        if (whoShutdown != null) {
            GossipManagement.instance().down(whoShutdown);
        }
    }
}
