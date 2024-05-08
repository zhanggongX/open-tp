package cn.opentp.gossip.handler;

import cn.opentp.gossip.core.GossipManager;
import cn.opentp.gossip.model.GossipMember;
import com.alibaba.fastjson2.JSON;

public class ShutdownMessageHandler implements MessageHandler {

    @Override
    public void handle(String cluster, String data, String from) {
        GossipMember whoShutdown = JSON.parseObject(data, GossipMember.class);
        if (whoShutdown != null) {
            GossipManager.getInstance().down(whoShutdown);
        }
    }
}
