package cn.opentp.gossip.util;

import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.GossipSettings;

public class CommonUtil {

    /**
     * 判定过期时间
     */
    public static long convictedTime() {
        long executeGossipTime = 500;
        GossipSettings setting = GossipApp.instance().setting();
        return ((convergenceCount() * (setting.getNetworkDelay() * 3L + executeGossipTime)) << 1) + setting.getGossipInterval();
    }

    /**
     * 判定趋同次数
     */
    public static int convergenceCount() {
        int size = GossipApp.instance().gossipNodeContext().endpointNodes().size();
        return (int) Math.floor(Math.log10(size) + Math.log(size) + 1);
    }
}
