package cn.opentp.client.core;

import cn.opentp.client.core.net.NettyClient;
import cn.opentp.client.core.report.ReportTask;

public class OpentpClientBootstrap {

    public void start() {
        NettyClient.start();
        new ReportTask().startReport();
    }

    public static void main(String[] args) {
        new OpentpClientBootstrap().start();
    }
}
