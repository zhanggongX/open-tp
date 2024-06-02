package cn.opentp.client.network.keepr;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("NettyConnectKeeperTaskTest")
class NettyConnectKeeperTaskTest {

    @BeforeAll
    public static void init() {
//        nettyBootstrap = new ThreadPoolReportService();
//        nettyBootstrap.startup();
    }

    @DisplayName("测试startup")
    @Test
    void startup() {
        // 连接保持器
        NettyConnectKeeperTask.keep();
    }
}