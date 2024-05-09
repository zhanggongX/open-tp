package cn.opentp.server.gossip;


import cn.opentp.gossip.GossipManagement;
import cn.opentp.gossip.GossipProperties;
import cn.opentp.gossip.GossipService;

public class Demo1 {

    public static void main(String[] args) {

        // 常用配置
        GossipProperties properties = new GossipProperties();
        properties.setCluster("opentp");
        properties.setHost("localhost");
        properties.setPort(9003);
        properties.setNodeId(null);
        properties.setClusterNodes("localhost:9001,localhost:9002");

        // 初始化
        GossipService.init(properties);

        // 开启服务
        GossipService.start();

        try {
            while (true) {
                Thread.sleep(5000);
                GossipManagement gossipManager = GossipManagement.instance();
                gossipManager.publish("hello world");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
