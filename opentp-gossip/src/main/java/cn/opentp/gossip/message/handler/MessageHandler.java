package cn.opentp.gossip.message.handler;

public interface MessageHandler {

    void handle(String cluster, String data, String from);
}
