package cn.opentp.gossip.core;


import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author silv
 */
public class MemoryMessageHolder implements GossipMessageHolder {

    private static final ConcurrentHashMap<String, GossipRegularMessage> messageCache = new ConcurrentHashMap<>();

    @Override
    public void add(GossipRegularMessage msg) {
        messageCache.putIfAbsent(msg.getId(), msg);
    }

    @Override
    public GossipRegularMessage acquire(String id) {
        return messageCache.get(id);
    }

    @Override
    public GossipRegularMessage remove(String id) {
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
