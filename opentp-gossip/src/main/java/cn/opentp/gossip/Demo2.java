package cn.opentp.gossip;

import cn.opentp.gossip.core.GossipService;
import cn.opentp.gossip.core.GossipSettings;
import cn.opentp.gossip.event.GossipListener;
import cn.opentp.gossip.model.GossipMember;
import cn.opentp.gossip.model.GossipState;
import cn.opentp.gossip.model.SeedMember;

import java.util.ArrayList;
import java.util.List;

public class Demo2 {

    public static void main(String[] args) {
        List<SeedMember> seedNodes = new ArrayList<>();
        SeedMember seed1 = new SeedMember();
        seed1.setCluster("cluster");
        seed1.setIpAddress("localhost");
        seed1.setPort(9001);

        SeedMember seed = new SeedMember();
        seed.setCluster("cluster");
        seed.setIpAddress("localhost");
        seed.setPort(9003);

        seedNodes.add(seed);
        seedNodes.add(seed1);

        try {
            GossipService gossipService = new GossipService("cluster", "localhost", 9002, null, seedNodes, new GossipSettings(), new GossipListener() {
                @Override
                public void gossipEvent(GossipMember member, GossipState state, Object payload) {
                    if (state == GossipState.RCV) {
                        System.out.println("member:" + member + "  state: " + state + " payload: " + payload);
                    }
                    if (state == GossipState.DOWN) {
                        System.out.println("[[[[[[[[[member:" + member + "  was down!!! ]]]]]]]]]");
                    }
                }
            });

            gossipService.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
