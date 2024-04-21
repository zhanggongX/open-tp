package cn.opentp.client;

import cn.opentp.client.net.NettyBootstrap;
import cn.opentp.client.net.keepr.NettyConnectKeeperTask;
import cn.opentp.client.net.worker.ThreadPoolStateExportTask;

public class OpentpClientBootstrap {

    public void start() {
        NettyBootstrap nettyBootstrap = new NettyBootstrap();
        nettyBootstrap.startup();

        // 连接保持器
        NettyConnectKeeperTask.startup(nettyBootstrap);
        // 线程信息上报
        ThreadPoolStateExportTask.startup();
    }
}
