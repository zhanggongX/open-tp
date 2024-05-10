package cn.opentp.gossip.handler;

public interface MessageHandler {

    void handle(String cluster, Object data, String from);
}
