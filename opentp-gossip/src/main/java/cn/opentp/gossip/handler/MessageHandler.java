package cn.opentp.gossip.handler;

public interface MessageHandler {

    void handle(String cluster, String data, String from);
}
