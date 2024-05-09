package cn.opentp.gossip.enums;

public enum GossipStateEnum {

    UP("up"),
    DOWN("down"),
    JOIN("join"),
    RCV("receive");

    private final String state;

    GossipStateEnum(String state) {
        this.state = state;
    }

}
