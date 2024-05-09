package cn.opentp.gossip;


public class Demo3 {

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
                GossipManager gossipManager = GossipManager.instance();
                gossipManager.publish("hello world");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
