package cn.opentp.gossip.gms;

import cn.opentp.gossip.net.IVerbHandler;
import cn.opentp.gossip.net.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class GossipShutdownVerbHandler implements IVerbHandler {

    private static final Logger log = LoggerFactory.getLogger(GossipShutdownVerbHandler.class);

    public void doVerb(Message message, String id) {
        InetSocketAddress from = message.getFrom();
        if (!GossiperApp.instance.isEnabled()) {
            log.debug("Ignoring shutdown message from {} because gossip is disabled", from);
            return;
        }
        FailureDetector.instance.forceConviction(from);
    }

}