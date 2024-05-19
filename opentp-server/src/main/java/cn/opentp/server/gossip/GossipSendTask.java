package cn.opentp.server.gossip;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.gossip.GossipEnvironment;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.network.report.ThreadPoolReportService;

import java.util.List;
import java.util.Map;

public class GossipSendTask implements Runnable {

    @Override
    public void run() {
        ThreadPoolReportService threadPoolReportService = OpentpApp.instance().reportService();
        Map<String, List<ClientInfo>> appKeyClientCache = threadPoolReportService.appKeyClientCache();
        GossipEnvironment.instance().publish(appKeyClientCache);
    }
}
