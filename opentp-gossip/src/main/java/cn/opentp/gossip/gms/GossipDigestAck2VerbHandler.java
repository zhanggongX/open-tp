package cn.opentp.gossip.gms;

import cn.opentp.gossip.io.util.FastByteArrayInputStream;
import cn.opentp.gossip.net.IVerbHandler;
import cn.opentp.gossip.net.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

public class GossipDigestAck2VerbHandler implements IVerbHandler {

    private static Logger log = LoggerFactory.getLogger(GossipDigestAck2VerbHandler.class);

    public void doVerb(Message message, String id) {
        if (log.isTraceEnabled()) {
            InetSocketAddress from = message.getFrom();
            log.trace("Received a GossipDigestAck2Message from {}", from);
        }

        byte[] bytes = message.getMessageBody();
        DataInputStream dis = new DataInputStream(new FastByteArrayInputStream(bytes));
        GossipDigestAck2Message gDigestAck2Message;
        try {
            gDigestAck2Message = GossipDigestAck2Message.serializer().deserialize(dis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<InetSocketAddress, EndpointState> remoteEpStateMap = gDigestAck2Message.getEndpointStateMap();
        /* Notify the Failure Detector */
        GossiperApp.instance.notifyFailureDetector(remoteEpStateMap);
        GossiperApp.instance.applyStateLocally(remoteEpStateMap);
    }
}
