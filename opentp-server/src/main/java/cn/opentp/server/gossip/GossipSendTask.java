package cn.opentp.server.gossip;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.auth.ServerInfo;
import cn.opentp.core.jackson.ServerInfoKeySerializer;
import cn.opentp.core.util.OpentpCoreJacksonUtils;
import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.util.GossipJacksonUtil;
import cn.opentp.server.OpentpApp;

import java.util.List;
import java.util.Map;

public class GossipSendTask implements Runnable {

    @Override
    public void run() {
        Map<ServerInfo, List<ClientInfo>> serverInfoListMap = OpentpApp.instance().clusterServerInfoCache();
        String jsonInfo = GossipJacksonUtil.toJSONString(serverInfoListMap);
        GossipApp.instance().publish(jsonInfo);
    }
}
