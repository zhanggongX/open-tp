package cn.opentp.gossip.gms;

import java.net.UnknownHostException;
import java.util.Map;

public interface FailureDetectorMBean {

    public void dumpInterArrivalTimes();

    public void setPhiConvictThreshold(int phi);

    public int getPhiConvictThreshold();

    public String getAllEndpointStates();

    public String getEndpointState(String address) throws UnknownHostException;

    public Map<String, String> getSimpleStates();
}
