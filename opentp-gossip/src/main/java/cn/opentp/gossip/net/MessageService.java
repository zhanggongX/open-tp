package cn.opentp.gossip.net;

import io.netty.buffer.ByteBuf;

public interface MessageService {

    void start(String host, int port);

    void handle(ByteBuf data);

    void send(String targetIp, Integer targetPort, ByteBuf data);

    void close();
}
