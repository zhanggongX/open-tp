package cn.opentp.gossip.gms;

import cn.opentp.gossip.io.util.FastByteArrayInputStream;
import cn.opentp.gossip.net.IVerbHandler;
import cn.opentp.gossip.net.Message;
import cn.opentp.gossip.net.MessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GossipDigestAckVerbHandler implements IVerbHandler {

    private static Logger log = LoggerFactory.getLogger(GossipDigestAckVerbHandler.class);

    public void doVerb(Message message, String id) {
        InetSocketAddress from = message.getFrom();
        if (log.isTraceEnabled())
            log.trace("Received a GossipDigestAckMessage from {}", from);
        if (!GossiperApp.instance.isEnabled()) {
            if (log.isTraceEnabled())
                log.trace("Ignoring GossipDigestAckMessage because gossip is disabled");
            return;
        }

        byte[] bytes = message.getMessageBody();
        DataInputStream dis = new DataInputStream(new FastByteArrayInputStream(bytes));

        try {
            GossipDigestAckMessage gDigestAckMessage = GossipDigestAckMessage.serializer().deserialize(dis);
            List<GossipDigest> gDigestList = gDigestAckMessage.getGossipDigestList();
            Map<InetSocketAddress, EndpointState> epStateMap = gDigestAckMessage.getEndpointStateMap();

            if (epStateMap.size() > 0) {
                /* Notify the Failure Detector */
                GossiperApp.instance.notifyFailureDetector(epStateMap);
                GossiperApp.instance.applyStateLocally(epStateMap);
            }

            /* Get the state required to send to this gossipee - construct GossipDigestAck2Message */
            Map<InetSocketAddress, EndpointState> deltaEpStateMap = new HashMap<InetSocketAddress, EndpointState>();
            for (GossipDigest gDigest : gDigestList) {
                InetSocketAddress addr = gDigest.getEndpoint();
                EndpointState localEpStatePtr = GossiperApp.instance.getStateForVersionBiggerThan(addr, gDigest.getMaxVersion());
                if (localEpStatePtr != null)
                    deltaEpStateMap.put(addr, localEpStatePtr);
            }

            GossipDigestAck2Message gDigestAck2 = new GossipDigestAck2Message(deltaEpStateMap);
            Message gDigestAck2Message = GossiperApp.instance.makeGossipDigestAck2Message(gDigestAck2);
            if (log.isTraceEnabled())
                log.trace("Sending a GossipDigestAck2Message to {}", from);
            MessagingService.instance().sendOneWay(gDigestAck2Message, from);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
