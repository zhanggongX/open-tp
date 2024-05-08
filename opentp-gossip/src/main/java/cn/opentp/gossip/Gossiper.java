package cn.opentp.gossip;

import cn.opentp.gossip.configuration.GossipProperties;
import cn.opentp.gossip.locator.SeedProvider;
import cn.opentp.gossip.locator.SimpleSeedProvider;
import cn.opentp.gossip.util.SocketAddressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class Gossiper {

    private static Logger logger = LoggerFactory.getLogger(Gossiper.class);

    private static GossipProperties properties;
    private static SeedProvider seedProvider;
    private static InetSocketAddress listenAddress;   // leave null so we can fall through to getLocalHost

    private static boolean hadInit = false;

    /**
     * Gossip 协议启动入口
     */
    public static void init(GossipProperties properties) {
        properties = properties;
        seedProvider = new SimpleSeedProvider(properties.getSeeds());
        parseConfigInfo();
        hadInit = true;
    }

    public static void checkHadInit() {
        if (!hadInit) {
            logger.error("未执行 cn.opentp.gossip.Gossiper.init 方法，服务终止！！！");
            System.exit(1);
        }
    }

    public static String getClusterName() {
        checkHadInit();
        return properties.getClusterName();
    }

    public static int getPhiConvictThreshold() {
        checkHadInit();
        return properties.getPhiConvictThreshold();
    }

    public static long getRpcTimeout() {
        checkHadInit();
        return properties.getRpcTimeoutInMs();
    }

    public static Integer getRingDelay() {
        checkHadInit();
        return properties.getRingDelayInMs();
    }

    public static Set<InetSocketAddress> getSeeds() {
        checkHadInit();
        return Collections.unmodifiableSet(new HashSet<InetSocketAddress>(seedProvider.getSeeds()));
    }

    public static InetSocketAddress getListenAddress() {
        checkHadInit();
        return listenAddress;
    }

    public static InetSocketAddress getBroadcastAddress() {
        checkHadInit();
        return listenAddress;
    }


    private static void parseConfigInfo() {
        String configListenAddress = properties.getListenAddress();

        try {
            if (configListenAddress == null) {
                throw new Exception("listen_address cannot be null!");
            }

            if (properties.getListenAddress().equals("0.0.0.0")) {
                throw new Exception("listen_address cannot be 0.0.0.0!");
            }

            try {
                listenAddress = SocketAddressUtil.parseSocketAddress(properties.getListenAddress());
            } catch (UnknownHostException e) {
                throw new Exception("Unknown listen_address '" + properties.getListenAddress() + "'");
            }

            if (seedProvider.getSeeds().isEmpty()) {
                throw new Exception("The seed provider lists no seeds.");
            }

        } catch (Exception e) {
            logger.error("Fatal configuration error", e);
            System.err.println(e.getMessage() + "\nFatal configuration error; unable to start server.  See log for stacktrace.");
            System.exit(1);
        }
    }
}
