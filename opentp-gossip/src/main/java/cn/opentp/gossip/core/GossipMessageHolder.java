package cn.opentp.gossip.core;

import java.util.Set;

public interface GossipMessageHolder {

    void add(GossipRegularMessage msg);

    GossipRegularMessage acquire(String id);

    GossipRegularMessage remove(String id);

    boolean contains(String id);

    boolean isEmpty();

    Set<String> list();
}
