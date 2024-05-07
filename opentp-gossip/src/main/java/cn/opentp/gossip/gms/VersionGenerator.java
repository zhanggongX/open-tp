package cn.opentp.gossip.gms;

import java.util.concurrent.atomic.AtomicInteger;

public class VersionGenerator {

    private static AtomicInteger _version = new AtomicInteger(0);

    public static int getNextVersion() {
        return _version.incrementAndGet();
    }
}
