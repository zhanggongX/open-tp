package cn.opentp.gossip;


import cn.opentp.gossip.event.GossipListener;
import cn.opentp.gossip.model.GossipMember;
import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.model.SeedNode;

import java.util.ArrayList;
import java.util.List;

public class Demo3 {

    public static void main(String[] args) {

        GossipProperties properties = new GossipProperties();
        properties.setCluster("opentp");
        properties.setHost("localhost");
        properties.setPort(9003);
        properties.setNodeId(null);
        properties.setClusterNode("localhost:9001,localhost:9002");

        GossipManager.init(properties);

        try {
            GossipService gossipService = new GossipService(new GossipListener() {
                @Override
                public void gossipEvent(GossipMember member, GossipStateEnum state, Object payload) {
                    if (state == GossipStateEnum.RCV) {
                        System.out.println("member:" + member + "  state: " + state + " payload: " + payload);
                    }
                    if (state == GossipStateEnum.DOWN) {
                        System.out.println("[[[[[[[[[member:" + member + "  was down!!! ]]]]]]]]]");
                    }
                }
            });

            gossipService.start();

            while (true) {
                Thread.sleep(5000);
                GossipManager gossipManager = GossipManager.instance();
                gossipManager.publish("hello world");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
