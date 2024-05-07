package cn.opentp.gossip.locator;

import java.net.InetSocketAddress;
import java.util.List;

public interface SeedProvider {

    List<InetSocketAddress> getSeeds();
}
