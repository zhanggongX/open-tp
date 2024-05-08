package cn.opentp.gossip.model;

public enum GossipState {

    UP("up"), DOWN("down"), JOIN("join"), RCV("receive");

    private final String state;

    GossipState(String state) {
        this.state = state;
    }

}
