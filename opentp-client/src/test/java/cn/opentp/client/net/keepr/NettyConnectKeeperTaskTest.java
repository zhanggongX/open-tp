package cn.opentp.client.net.keepr;

import cn.opentp.client.net.NettyBootstrap;
import org.junit.jupiter.api.*;

@DisplayName("NettyConnectKeeperTaskTest")
class NettyConnectKeeperTaskTest {

    private static NettyBootstrap nettyBootstrap;

    @BeforeAll
    public static void init() {
        nettyBootstrap = new NettyBootstrap();
//        nettyBootstrap.startup();
    }

    @DisplayName("测试startup")
    @Test
    void startup() {
        // 连接保持器
        NettyConnectKeeperTask.startup(nettyBootstrap);
    }
}