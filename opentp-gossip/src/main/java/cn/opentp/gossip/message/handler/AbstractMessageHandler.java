package cn.opentp.gossip.message.handler;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.node.GossipNode;
import cn.opentp.gossip.node.HeartbeatState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public abstract class AbstractMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(AbstractMessageHandler.class);

    protected void apply2LocalState(Map<GossipNode, HeartbeatState> endpointMembers) {
        Set<GossipNode> keys = endpointMembers.keySet();
        for (GossipNode m : keys) {
            if (GossipApp.instance().selfNode().equals(m)) {
                continue;
            }

            try {
                HeartbeatState localState = GossipApp.instance().gossipNodeContext().endpointNodes().get(m);
                HeartbeatState remoteState = endpointMembers.get(m);

                if (localState != null) {
                    long localHeartbeatTime = localState.getHeartbeatTime();
                    long remoteHeartbeatTime = remoteState.getHeartbeatTime();
                    if (remoteHeartbeatTime > localHeartbeatTime) {
                        remoteStateReplaceLocalState(m, remoteState);
                    } else if (remoteHeartbeatTime == localHeartbeatTime) {
                        long localVersion = localState.getVersion();
                        long remoteVersion = remoteState.getVersion();
                        if (remoteVersion > localVersion) {
                            remoteStateReplaceLocalState(m, remoteState);
                        }
                    }
                } else {
                    remoteStateReplaceLocalState(m, remoteState);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private void remoteStateReplaceLocalState(GossipNode member, HeartbeatState remoteState) {
        if (member.getState() == GossipStateEnum.UP) {
            GossipApp.instance().gossipNodeContext().up(member);
        }
        if (member.getState() == GossipStateEnum.DOWN) {
            GossipApp.instance().gossipNodeContext().down(member);
        }
        if (GossipApp.instance().gossipNodeContext().endpointNodes().containsKey(member)) {
            GossipApp.instance().gossipNodeContext().endpointNodes().remove(member);
        }
        GossipApp.instance().gossipNodeContext().endpointNodes().put(member, remoteState);
    }
}