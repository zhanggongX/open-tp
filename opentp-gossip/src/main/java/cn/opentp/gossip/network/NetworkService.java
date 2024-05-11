package cn.opentp.gossip.network;

public interface NetworkService {

    void start(String host, int port);

    void handle(String data);

    void send(String targetIp, Integer targetPort, Object data);

    void close();
}
