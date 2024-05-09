package cn.opentp.gossip.core;

import cn.opentp.gossip.model.RegularMessage;

import java.util.Set;

public interface MessageManager {

    void add(RegularMessage msg);

    RegularMessage acquire(String id);

    RegularMessage remove(String id);

    boolean contains(String id);

    boolean isEmpty();

    Set<String> list();
}
