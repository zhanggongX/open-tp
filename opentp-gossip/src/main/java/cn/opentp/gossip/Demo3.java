package cn.opentp.gossip;


import cn.opentp.gossip.event.GossipListener;
import cn.opentp.gossip.model.GossipMember;
import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.model.SeedNode;

import java.util.ArrayList;
import java.util.List;

public class Demo3 {

    public static void main(String[] args) {
        List<SeedNode> seedNodes = new ArrayList<>();
//        SeedNode seed1 = new SeedNode();
//        seed1.setCluster("cluster");
//        seed1.setIpAddress("localhost");
//        seed1.setPort(9002);
//
//        SeedNode seed = new SeedNode();
//        seed.setCluster("cluster");
//        seed.setIpAddress("localhost");
//        seed.setPort(9001);
//
//        seedNodes.add(seed);
//        seedNodes.add(seed1);

        try {
            GossipService gossipService = new GossipService("cluster", "localhost", 9003, null, seedNodes, new GossipSettings(), new GossipListener() {
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
                gossipService.getGossipManager().publish("hello world");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
