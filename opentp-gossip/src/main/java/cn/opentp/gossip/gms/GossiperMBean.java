package cn.opentp.gossip.gms;

import java.net.UnknownHostException;

public interface GossiperMBean {

    public long getEndpointDowntime(String address) throws UnknownHostException;

    public int getCurrentGenerationNumber(String address) throws UnknownHostException;
}