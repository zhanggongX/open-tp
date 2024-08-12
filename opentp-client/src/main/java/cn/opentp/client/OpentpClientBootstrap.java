package cn.opentp.client;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.client.network.keepr.NettyConnectKeeperTask;
import cn.opentp.client.network.worker.ThreadPoolStateExportTask;

public class OpentpClientBootstrap {

    public void startup() {

        Configuration._cfg().reportService().startup();

        // 连接保持器
        NettyConnectKeeperTask.keep();
        // 线程信息上报
        ThreadPoolStateExportTask.report();
    }
}
