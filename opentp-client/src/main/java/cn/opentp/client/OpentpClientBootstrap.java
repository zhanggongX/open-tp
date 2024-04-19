package cn.opentp.client;

import cn.opentp.client.net.NettyClient;
import cn.opentp.client.report.ReportTask;

public class OpentpClientBootstrap {

    public void start() {
        Thread start = NettyClient.start();
        ReportTask.startReport();
    }
}
