package cn.opentp.gossip.message.holder;


import cn.opentp.gossip.message.GossipMessage;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class MemoryGossipMessageContext implements GossipMessageContext {

    private static final ConcurrentHashMap<String, GossipMessage> messageCache = new ConcurrentHashMap<>();

    @Override
    public void add(GossipMessage msg) {
        messageCache.putIfAbsent(msg.getId(), msg);
    }

    @Override
    public GossipMessage acquire(String id) {
        return messageCache.get(id);
    }

    @Override
    public GossipMessage remove(String id) {
        return messageCache.remove(id);
    }

    @Override
    public boolean contains(String id) {
        return messageCache.containsKey(id);
    }

    @Override
    public boolean isEmpty() {
        return messageCache.isEmpty();
    }

    @Override
    public Set<String> list() {
        return messageCache.keySet();
    }
}
