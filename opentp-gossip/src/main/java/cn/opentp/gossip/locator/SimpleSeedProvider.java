package cn.opentp.gossip.locator;

import cn.opentp.gossip.util.SocketAddressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleSeedProvider implements SeedProvider {

    private static final Logger logger = LoggerFactory.getLogger(SimpleSeedProvider.class);

    private final List<InetSocketAddress> seeds;

    public SimpleSeedProvider(String seedAddresses) {
        String[] hosts = seedAddresses.split(",", -1);
        seeds = new ArrayList<>(hosts.length);
        for (String host : hosts) {
            try {
                seeds.add(SocketAddressUtil.parseSocketAddress(host.trim()));
            } catch (UnknownHostException ex) {
                logger.warn("Seed provider couldn't lookup host {}", host);
            }
        }
    }

    public List<InetSocketAddress> getSeeds() {
        return Collections.unmodifiableList(seeds);
    }

    public void addSeed(InetSocketAddress address) {
        if (!seeds.contains(address))
            seeds.add(address);
    }
}
