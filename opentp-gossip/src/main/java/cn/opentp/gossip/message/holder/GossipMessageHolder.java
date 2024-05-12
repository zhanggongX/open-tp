package cn.opentp.gossip.message.holder;

import cn.opentp.gossip.message.GossipMessage;

import java.util.Set;

public interface GossipMessageHolder {

    void add(GossipMessage message);

    GossipMessage acquire(String messageId);

    GossipMessage remove(String messageId);

    boolean contains(String messageId);

    boolean isEmpty();

    Set<String> list();
}
