package cn.opentp.server.gossip;


import cn.opentp.gossip.GossipApp;
import cn.opentp.gossip.GossipProperties;
import cn.opentp.gossip.Gossip;

public class Demo2 {

    public static void main(String[] args) {

        // 常用配置
        GossipProperties properties = new GossipProperties();
        properties.setCluster("opentp");
        properties.setHost("localhost");
        properties.setPort(9002);
        properties.setNodeId(null);
        properties.setClusterNodes("localhost:9001,localhost:9003");

        // 初始化
        Gossip.init(properties);

        // 开启服务
        Gossip.start();

        try {
            while (true) {
                Thread.sleep(5000);
                GossipApp gossipManager = GossipApp.instance();
                gossipManager.publish("hello world");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
