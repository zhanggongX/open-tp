package cn.opentp.server.infrastructure.gossip;

import cn.opentp.gossip.GossipEnvironment;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.connection.ConnectionImpl;
import cn.opentp.server.network.receive.ThreadPoolReceiveService;

import java.util.List;
import java.util.Map;

public class GossipSendTask implements Runnable {

    @Override
    public void run() {
        ThreadPoolReceiveService threadPoolReportService = OpentpApp.instance().receiveService();
        Map<String, List<ConnectionImpl>> appKeyConnectCache = threadPoolReportService.appKeyConnectCache();
        GossipEnvironment.instance().publish(appKeyConnectCache);
    }
}
