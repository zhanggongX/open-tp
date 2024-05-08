package cn.opentp.gossip.gms;

import cn.opentp.gossip.Gossiper;
import cn.opentp.gossip.io.util.FastByteArrayInputStream;
import cn.opentp.gossip.net.IVerbHandler;
import cn.opentp.gossip.net.Message;
import cn.opentp.gossip.net.MessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

public class GossipDigestSynVerbHandler implements IVerbHandler {

    private static Logger log = LoggerFactory.getLogger(GossipDigestSynVerbHandler.class);

    public void doVerb(Message message, String id) {
        InetSocketAddress from = message.getFrom();
        if (log.isTraceEnabled())
            log.trace("Received a GossipDigestSynMessage from {}", from);
        if (!GossiperApp.instance.isEnabled()) {
            if (log.isTraceEnabled())
                log.trace("Ignoring GossipDigestSynMessage because gossip is disabled");
            return;
        }

        byte[] bytes = message.getMessageBody();
        DataInputStream dis = new DataInputStream(new FastByteArrayInputStream(bytes));

        try {
            GossipDigestSynMessage gDigestMessage = GossipDigestSynMessage.serializer().deserialize(dis);
            /* If the message is from a different cluster throw it away. */
            if (!gDigestMessage.clusterId_.equals(Gossiper.getClusterName())) {
                log.warn("ClusterName mismatch from " + from + " " + gDigestMessage.clusterId_ + "!=" + Gossiper.getClusterName());
                return;
            }

            List<GossipDigest> gDigestList = gDigestMessage.getGossipDigests();
            if (log.isTraceEnabled()) {
                StringBuilder sb = new StringBuilder();
                for (GossipDigest gDigest : gDigestList) {
                    sb.append(gDigest);
                    sb.append(" ");
                }
                log.trace("Gossip syn digests are : " + sb.toString());
            }
            /* Notify the Failure Detector */
            GossiperApp.instance.notifyFailureDetector(gDigestList);

            doSort(gDigestList);

            List<GossipDigest> deltaGossipDigestList = new ArrayList<GossipDigest>();
            Map<InetSocketAddress, EndpointState> deltaEpStateMap = new HashMap<InetSocketAddress, EndpointState>();
            GossiperApp.instance.examineGossiper(gDigestList, deltaGossipDigestList, deltaEpStateMap);

            GossipDigestAckMessage gDigestAck = new GossipDigestAckMessage(deltaGossipDigestList, deltaEpStateMap);
            Message gDigestAckMessage = GossiperApp.instance.makeGossipDigestAckMessage(gDigestAck);
            if (log.isTraceEnabled())
                log.trace("Sending a GossipDigestAckMessage to {}", from);
            MessagingService.instance().sendOneWay(gDigestAckMessage, from);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * First construct a map whose key is the endpoint in the GossipDigest and the value is the
     * GossipDigest itself. Then build a list of version differences i.e difference between the
     * version in the GossipDigest and the version in the local state for a given InetAddress.
     * Sort this list. Now loop through the sorted list and retrieve the GossipDigest corresponding
     * to the endpoint from the map that was initially constructed.
     */
    private void doSort(List<GossipDigest> gDigestList) {
        /* Construct a map of endpoint to GossipDigest. */
        Map<InetSocketAddress, GossipDigest> epToDigestMap = new HashMap<InetSocketAddress, GossipDigest>();
        for (GossipDigest gDigest : gDigestList) {
            epToDigestMap.put(gDigest.getEndpoint(), gDigest);
        }

        /*
         * These digests have their maxVersion set to the difference of the version
         * of the local EndpointState and the version found in the GossipDigest.
         */
        List<GossipDigest> diffDigests = new ArrayList<GossipDigest>(gDigestList.size());
        for (GossipDigest gDigest : gDigestList) {
            InetSocketAddress ep = gDigest.getEndpoint();
            EndpointState epState = GossiperApp.instance.getEndpointStateForEndpoint(ep);
            int version = (epState != null) ? GossiperApp.instance.getMaxEndpointStateVersion(epState) : 0;
            int diffVersion = Math.abs(version - gDigest.getMaxVersion());
            diffDigests.add(new GossipDigest(ep, gDigest.getGeneration(), diffVersion));
        }

        gDigestList.clear();
        Collections.sort(diffDigests);
        int size = diffDigests.size();
        /*
         * Report the digests in descending order. This takes care of the endpoints
         * that are far behind w.r.t this local endpoint
         */
        for (int i = size - 1; i >= 0; --i) {
            gDigestList.add(epToDigestMap.get(diffDigests.get(i).getEndpoint()));
        }
    }
}
