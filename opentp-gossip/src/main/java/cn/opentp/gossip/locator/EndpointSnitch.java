package cn.opentp.gossip.locator;

import cn.opentp.gossip.enums.ApplicationStateEnum;
import cn.opentp.gossip.gms.EndpointState;
import cn.opentp.gossip.gms.IEndpointStateChangeSubscriber;
import cn.opentp.gossip.gms.VersionedValue;

import java.net.InetSocketAddress;

public class EndpointSnitch implements IEndpointStateChangeSubscriber {

    @Override
    public void onAlive(InetSocketAddress endpoint, EndpointState state) {
        // TODO Auto-generated method stub
        System.out.println("onAlive!" + endpoint);
    }

    @Override
    public void onChange(InetSocketAddress endpoint, ApplicationStateEnum state,
                         VersionedValue value) {
        // TODO Auto-generated method stub
        System.out.println("onChange!" + endpoint);
    }

    @Override
    public void onDead(InetSocketAddress endpoint, EndpointState state) {
        // TODO Auto-generated method stub
        System.out.println("onDead!" + endpoint);
    }

    @Override
    public void onJoin(InetSocketAddress endpoint, EndpointState epState) {
        // TODO Auto-generated method stub
        System.out.println("onJoin!" + endpoint);
    }

    @Override
    public void onRemove(InetSocketAddress endpoint) {
        // TODO Auto-generated method stub
        System.out.println("onRemove!" + endpoint);
    }

    @Override
    public void onRestart(InetSocketAddress endpoint, EndpointState state) {
        // TODO Auto-generated method stub
        System.out.println("onRestart!" + endpoint);
    }

}
