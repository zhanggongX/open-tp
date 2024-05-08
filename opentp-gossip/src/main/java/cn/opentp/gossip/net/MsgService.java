package cn.opentp.gossip.net;

import io.netty.buffer.ByteBuf;

public interface MsgService {

    void listen(String ipAddress, int port);

    void handleMsg(ByteBuf data);

    void sendMsg(String targetIp, Integer targetPort, ByteBuf data);

    void unListen();
}
