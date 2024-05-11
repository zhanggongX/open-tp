package cn.opentp.gossip.message.service;

public interface MessageService {

    void start(String host, int port);

    void handle(String data);

    void send(String targetIp, Integer targetPort, Object data);

    void close();
}
