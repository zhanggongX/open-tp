package cn.opentp.gossip.message.handler;

public interface MessageHandler {

    void handle(String cluster, byte[] data, String from);
}
