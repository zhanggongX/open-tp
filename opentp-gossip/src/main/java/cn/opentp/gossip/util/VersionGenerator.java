package cn.opentp.gossip.util;

import java.util.concurrent.atomic.AtomicLong;

public class VersionGenerator {

    private static final AtomicLong version = new AtomicLong(0);

    public static long nextVersion() {
        return version.incrementAndGet();
    }
}
