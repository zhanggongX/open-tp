package cn.opentp.gossip.util;

import cn.opentp.gossip.GossipEnvironment;
import cn.opentp.gossip.GossipSettings;

public class GossipUtil {

    /**
     * 判定过期时间
     */
    public static long convictedTime() {
        long executeGossipTime = 500;
        GossipSettings setting = GossipEnvironment.instance().setting();
        return ((fanOut() * (setting.getNetworkDelay() * 3L + executeGossipTime)) << 1) + setting.getGossipInterval();
    }

    /**
     * 计算扇出 fanOut
     * Fan-out is the distribution of messages by a service or message router to multiple users, often simultaneously.
     * 扇出是指由一个服务或消息路由器向多个用户分发消息，通常是同时分发。
     */
    public static int fanOut() {
        int size = GossipEnvironment.instance().gossipNodeContext().clusterNodes().size();
        return (int) Math.floor(Math.log10(size) + Math.log(size) + 1);
    }
}
