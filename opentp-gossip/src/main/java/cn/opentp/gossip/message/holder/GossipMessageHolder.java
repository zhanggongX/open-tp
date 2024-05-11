package cn.opentp.gossip.message.holder;

import cn.opentp.gossip.message.GossipMessage;

import java.util.Set;

public interface GossipMessageHolder {

    void add(GossipMessage msg);

    GossipMessage acquire(String id);

    GossipMessage remove(String id);

    boolean contains(String id);

    boolean isEmpty();

    Set<String> list();
}
