package cn.opentp.gossip.gms;

import java.net.InetSocketAddress;

public interface IFailureDetector {

    /**
     * Failure Detector's knowledge of whether a node is up or
     * down.
     *
     * @param ep endpoint in question.
     * @return true if UP and false if DOWN.
     */
    public boolean isAlive(InetSocketAddress ep);

    /**
     * Clear any existing interval timings for this endpoint
     *
     * @param ep
     */
    public void clear(InetSocketAddress ep);

    /**
     * This method is invoked by any entity wanting to interrogate the status of an endpoint.
     * In our case it would be the Gossiper. The Failure Detector will then calculate Phi and
     * deem an endpoint as suspicious or alive as explained in the Hayashibara paper.
     * <p>
     * param ep endpoint for which we interpret the inter arrival times.
     */
    public void interpret(InetSocketAddress ep);

    /**
     * This method is invoked by the receiver of the heartbeat. In our case it would be
     * the Gossiper. Gossiper inform the Failure Detector on receipt of a heartbeat. The
     * FailureDetector will then sample the arrival time as explained in the paper.
     * <p>
     * param ep endpoint being reported.
     */
    public void report(InetSocketAddress ep);

    /**
     * remove endpoint from failure detector
     */
    public void remove(InetSocketAddress ep);

    /**
     * force conviction of endpoint in the failure detector
     */
    public void forceConviction(InetSocketAddress ep);

    /**
     * Register interest for Failure Detector events.
     *
     * @param listener implementation of an application provided IFailureDetectionEventListener
     */
    public void registerFailureDetectionEventListener(IFailureDetectionEventListener listener);

    /**
     * Un-register interest for Failure Detector events.
     *
     * @param listener implementation of an application provided IFailureDetectionEventListener
     */
    public void unregisterFailureDetectionEventListener(IFailureDetectionEventListener listener);
}
