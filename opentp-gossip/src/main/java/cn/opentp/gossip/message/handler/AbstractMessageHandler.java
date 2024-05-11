package cn.opentp.gossip.message.handler;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.enums.GossipStateEnum;
import cn.opentp.gossip.model.GossipNode;
import cn.opentp.gossip.model.HeartbeatState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AbstractMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(AbstractMessageHandler.class);

    protected void up(GossipNode member) {
        GossipApp gossipApp = GossipApp.instance();
        ReentrantReadWriteLock.WriteLock writeLock = gossipApp.lock().writeLock();
        try {
            writeLock.lock();
            member.setState(GossipStateEnum.UP);
            if (!gossipApp.liveNodes().contains(member)) {
                gossipApp.liveNodes().add(member);
            }
            if (gossipApp.candidateMembers().containsKey(member)) {
                gossipApp.candidateMembers().remove(member);
            }
            if (gossipApp.deadNodes().contains(member)) {
                gossipApp.deadNodes().remove(member);
                log.info("up ~~");
                if (!member.equals(gossipApp.selfNode())) {
                    gossipApp.listener().gossipEvent(member, GossipStateEnum.JOIN, null);
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            writeLock.unlock();
        }

    }

    protected void down(GossipNode member) {
        log.info("down ~~");
        GossipApp gossipApp = GossipApp.instance();
        ReentrantReadWriteLock.WriteLock writeLock = gossipApp.lock().writeLock();
        try {
            writeLock.lock();
            member.setState(GossipStateEnum.DOWN);
            gossipApp.liveNodes().remove(member);
            if (!gossipApp.deadNodes().contains(member)) {
                gossipApp.deadNodes().add(member);
            }
//            clearExecutor.schedule(() -> clearMember(member), getSettings().getDeleteThreshold() * getSettings().getGossipInterval(), TimeUnit.MILLISECONDS);
            gossipApp.listener().gossipEvent(member, GossipStateEnum.DOWN, null);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            writeLock.unlock();
        }
    }

    protected void apply2LocalState(Map<GossipNode, HeartbeatState> endpointMembers) {
        Set<GossipNode> keys = endpointMembers.keySet();
        for (GossipNode m : keys) {
            if (GossipApp.instance().selfNode().equals(m)) {
                continue;
            }

            try {
                HeartbeatState localState = GossipApp.instance().endpointNodeCache().get(m);
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
            up(member);
        }
        if (member.getState() == GossipStateEnum.DOWN) {
            down(member);
        }
        if (GossipApp.instance().endpointNodeCache().containsKey(member)) {
            GossipApp.instance().endpointNodeCache().remove(member);
        }
        GossipApp.instance().endpointNodeCache().put(member, remoteState);
    }
}
