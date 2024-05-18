package cn.opentp.gossip.network;

import cn.opentp.gossip.message.MessagePayload;

public interface NetworkService {

    void start(String host, int port);

    void handle(MessagePayload data);

    void send(String targetIp, Integer targetPort, Object data);

    void close();
}
