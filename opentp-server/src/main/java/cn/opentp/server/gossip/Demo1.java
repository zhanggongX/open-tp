package cn.opentp.server.gossip;


import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.GossipProperties;
import cn.opentp.gossip.Gossip;
import cn.opentp.gossip.net.MessageService;

public class Demo1 {

    public static void main(String[] args) {

        // 常用配置
        GossipProperties properties = new GossipProperties();
        properties.setCluster("opentp");
        properties.setHost("localhost");
        properties.setPort(9001);
        properties.setNodeId(null);
        properties.setClusterNodes("localhost:9002,localhost:9003");
//        properties.setGossipInterval(5000);

        // 初始化
        Gossip.init(properties);

        // 开启服务
        Gossip.start();

        try {
            while (true) {
                Thread.sleep(5000);
                GossipApp gossipApp = GossipApp.instance();
                gossipApp.publish("demo1");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
