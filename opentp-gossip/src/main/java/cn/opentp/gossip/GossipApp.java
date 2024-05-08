package cn.opentp.gossip;

public class GossipApp {

    private GossipProperties properties;

    private static final GossipApp instance = new GossipApp();

    private GossipApp() {
    }

    public static GossipApp instance() {
        return instance;
    }
}
