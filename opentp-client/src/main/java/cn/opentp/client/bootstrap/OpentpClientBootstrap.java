package cn.opentp.client.bootstrap;

import cn.opentp.client.net.NettyClient;
import cn.opentp.client.report.ReportTask;

public class OpentpClientBootstrap {

    public void start() {
        NettyClient.start();
        new ReportTask().startReport();
    }

    public static void main(String[] args) {
        new OpentpClientBootstrap().start();
    }
}
