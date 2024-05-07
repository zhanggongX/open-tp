package cn.opentp.gossip.test;

import cn.opentp.gossip.Gossiper;
import cn.opentp.gossip.configuration.GossipProperties;
import cn.opentp.gossip.enums.ApplicationStateEnum;
import cn.opentp.gossip.gms.VersionedValue;
import cn.opentp.gossip.service.GossipService;

import java.io.IOException;

public class Demo {

    public static void main(String[] args) throws IOException, InterruptedException {

        Gossiper.init(new GossipProperties("localhost:9001", "localhost:9001,localhost:9002"));
        //TODO
        final GossipService service = new GossipService();

        service.getGossiper().register(new EndpointSnitch());
        service.getGossiper().register(new IApplicationStateStarting() {

            public void gossiperStarting() {
                service.getGossiper().addLocalApplicationState(ApplicationStateEnum.LOAD, VersionedValue.VersionedValueFactory.instance.load(7.1));
                service.getGossiper().addLocalApplicationState(ApplicationStateEnum.WEIGHT, VersionedValue.VersionedValueFactory.instance.weight(5));

            }
        });

        service.start((int) (System.currentTimeMillis() / 1000));
//    	new DaemonTest1().printEndpointStates();
//
//    	Thread.sleep(1000*15);
//    	executor.shutdownNow();
//    	executor2.shutdownNow();
//    	service.stop();

    }
}
