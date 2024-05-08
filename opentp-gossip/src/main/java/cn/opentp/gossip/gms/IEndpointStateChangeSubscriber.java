package cn.opentp.gossip.gms;

import cn.opentp.gossip.enums.ApplicationStateEnum;

import java.net.InetSocketAddress;

public interface IEndpointStateChangeSubscriber {

    public void onJoin(InetSocketAddress endpoint, EndpointState epState);

    public void onChange(InetSocketAddress endpoint, ApplicationStateEnum state, VersionedValue value);

    public void onAlive(InetSocketAddress endpoint, EndpointState state);

    public void onDead(InetSocketAddress endpoint, EndpointState state);

    public void onRemove(InetSocketAddress endpoint);

    public void onRestart(InetSocketAddress endpoint, EndpointState state);
}
