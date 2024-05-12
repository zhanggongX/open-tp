package cn.opentp.server.gossip;


import cn.opentp.gossip.Gossip;
import cn.opentp.gossip.GossipProperties;

public class Demo2 {

    public static void main(String[] args) {

        // 常用配置
        GossipProperties properties = new GossipProperties();
        properties.setCluster("opentp");
        properties.setHost("localhost");
        properties.setPort(9003);
        properties.setNodeId(null);
        properties.setGossipInterval(10000);
        properties.setClusterNodes("localhost:9001,localhost:9002");

        // 初始化
        Gossip.init(properties);

        // 开启服务
        Gossip.start();

//        try {
//            while (true) {
//                Thread.sleep(50000);
//                GossipApp gossipManager = GossipApp.instance();
//                gossipManager.publish("hello world");
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }
}
