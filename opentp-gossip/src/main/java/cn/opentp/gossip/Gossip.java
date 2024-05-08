package cn.opentp.gossip;

import cn.opentp.gossip.util.SocketAddressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Gossip {

    private static final Logger log = LoggerFactory.getLogger(Gossip.class);

    // 属性信息
    private static GossipProperties properties;
    // 全局实例
    private static final GossipApp gossipApp = GossipApp.instance();
    // 初始化标记
    private static boolean hadInit = false;

    public static void init(GossipProperties gossipProperties) {
        properties = gossipProperties;
//        seedProvider = new SimpleSeedProvider(conf.getSeeds());
        parseConfigInfo();
        hadInit = true;
    }

    public static void checkHadInit() {
        if (!hadInit) {
            log.error("启动错误，Gossip 尚未初始化！");
            System.exit(1);
        }
    }

    public static GossipProperties properties() {
        checkHadInit();
        return properties;
    }

    private static void parseConfigInfo() {
        try {
            String listenAddress = properties.getListenAddress();
            if (listenAddress == null) {
                throw new Exception("listen_address cannot be null!");
            }

            if (listenAddress.equals("0.0.0.0")) {
                throw new Exception("listen_address cannot be 0.0.0.0!");
            }
            try {
                InetSocketAddress inetSocketAddress = SocketAddressUtil.parseSocketAddress(listenAddress);
                properties.setInetSocketAddress(inetSocketAddress);
            } catch (UnknownHostException e) {
                throw new Exception("Unknown listen_address '" + listenAddress + "'");
            }
        } catch (Exception e) {
            log.error("Fatal configuration error", e);
            System.exit(1);
        }
    }
}
