package cn.opentp.client.bootstrap;

import cn.opentp.client.net.NettyClient;
import cn.opentp.client.report.ReportTask;

public class OpentpClientBootstrap {

    public void start() {
        NettyClient.start();
        ReportTask.startReport();
    }
}
