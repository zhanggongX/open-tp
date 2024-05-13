package cn.opentp.gossip.message.holder;

import cn.opentp.gossip.message.GossipMessage;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class MemoryGossipMessageHolder implements GossipMessageHolder {

    private static final ConcurrentHashMap<String, GossipMessage> messageCache = new ConcurrentHashMap<>();

    @Override
    public void add(GossipMessage message) {
        messageCache.putIfAbsent(message.getMessageId(), message);
    }

    @Override
    public GossipMessage acquire(String messageId) {
        return messageCache.get(messageId);
    }

    @Override
    public GossipMessage remove(String messageId) {
        return messageCache.remove(messageId);
    }

    @Override
    public boolean contains(String messageId) {
        return messageCache.containsKey(messageId);
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
