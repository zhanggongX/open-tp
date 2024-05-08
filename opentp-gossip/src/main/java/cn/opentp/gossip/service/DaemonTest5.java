package cn.opentp.gossip.service;

import cn.opentp.gossip.Gossiper;
import cn.opentp.gossip.concurrent.DebuggableScheduledThreadPoolExecutor;
import cn.opentp.gossip.configuration.GossipProperties;
import cn.opentp.gossip.enums.ApplicationStateEnum;
import cn.opentp.gossip.gms.EndpointState;
import cn.opentp.gossip.gms.GossiperApp;
import cn.opentp.gossip.gms.VersionedValue;
import cn.opentp.gossip.gms.VersionedValue.VersionedValueFactory;
import cn.opentp.gossip.locator.EndpointSnitch;
import cn.opentp.gossip.locator.IApplicationStateStarting;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class DaemonTest5 {
    private void printEndpointStates() throws IOException {
        DebuggableScheduledThreadPoolExecutor executor = new DebuggableScheduledThreadPoolExecutor("abc");
        executor.scheduleWithFixedDelay(new Runnable() {
                                            public void run() {

                                                Set<Entry<InetSocketAddress, EndpointState>> set = GossiperApp.instance.getEndpointStates();
                                                for (Iterator<Entry<InetSocketAddress, EndpointState>> iterator = set.iterator(); iterator.hasNext(); ) {
                                                    Entry<InetSocketAddress, EndpointState> entry = iterator.next();
                                                    System.out.println("key:" + entry.getKey() + ", value:" + entry.getValue());

                                                    EndpointState endpoint = entry.getValue();
                                                    for (Entry<ApplicationStateEnum, VersionedValue> entry2 : endpoint.getApplicationStateMapEntrySet()) {
                                                        System.out.println("VersionedValue----key:" + entry2.getKey() + ", value:" + entry2.getValue());
                                                    }
                                                }
                                                System.out.println("=======================");

                                                Set<InetSocketAddress> liveset = GossiperApp.instance.getLiveMembers();
                                                for (Iterator<InetSocketAddress> iterator = liveset.iterator(); iterator.hasNext(); ) {
                                                    InetSocketAddress inetAddress = (InetSocketAddress) iterator.next();
                                                    System.out.println(inetAddress);
                                                }
                                            }
                                        },
                GossiperApp.intervalInMillis,
                GossiperApp.intervalInMillis,
                TimeUnit.MILLISECONDS);

    }

    public static void main(String[] args) throws IOException {

        Gossiper.init(new GossipProperties("localhost:9005", "localhost:9001,localhost:9002"));
        //TODO
        final GossipService service = new GossipService();

        service.getGossiper().register(new EndpointSnitch());
        service.getGossiper().register(new IApplicationStateStarting() {
            public void gossiperStarting() {
                service.getGossiper().addLocalApplicationState(ApplicationStateEnum.LOAD, VersionedValueFactory.instance.load(7.1));
                service.getGossiper().addLocalApplicationState(ApplicationStateEnum.WEIGHT, VersionedValueFactory.instance.weight(5));

            }
        });

        service.start((int) (System.currentTimeMillis() / 1000));

//    	new DaemonTest5().printEndpointStates();


    }
}
