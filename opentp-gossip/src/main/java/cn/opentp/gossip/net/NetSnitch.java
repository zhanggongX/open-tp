package cn.opentp.gossip.net;

import cn.opentp.gossip.enums.ApplicationStateEnum;
import cn.opentp.gossip.gms.EndpointState;
import cn.opentp.gossip.gms.IEndpointStateChangeSubscriber;
import cn.opentp.gossip.gms.VersionedValue;

import java.net.InetSocketAddress;

public class NetSnitch implements IEndpointStateChangeSubscriber {

    @Override
    public void onAlive(InetSocketAddress endpoint, EndpointState state) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onChange(InetSocketAddress endpoint, ApplicationStateEnum state,
                         VersionedValue value) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDead(InetSocketAddress endpoint, EndpointState state) {
        // TODO Auto-generated method stub
        MessagingService.instance().convict(endpoint);  //关闭发送消息的socket连接
    }

    @Override
    public void onJoin(InetSocketAddress endpoint, EndpointState epState) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onRemove(InetSocketAddress endpoint) {
        // TODO Auto-generated method stub
        MessagingService.instance().convict(endpoint);  //关闭发送消息的socket连接
    }

    @Override
    public void onRestart(InetSocketAddress endpoint, EndpointState state) {
        // TODO Auto-generated method stub
    }
}
