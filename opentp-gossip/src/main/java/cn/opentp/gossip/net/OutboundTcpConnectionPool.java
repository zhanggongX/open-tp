package cn.opentp.gossip.net;

import java.net.InetSocketAddress;


public class OutboundTcpConnectionPool {
    // pointer for the real Address.
    private final InetSocketAddress id;
    public final OutboundTcpConnection ackCon;

    OutboundTcpConnectionPool(InetSocketAddress remoteEp) {
        id = remoteEp;
        ackCon = new OutboundTcpConnection(this);
        ackCon.start();
    }

    /**
     * returns the appropriate connection based on message type.
     * returns null if a connection could not be established.
     */
    OutboundTcpConnection getConnection(Message msg) {
        return ackCon;
    }

    InetSocketAddress endPoint() {
        return id;
    }

    synchronized void reset() {
        ackCon.closeSocket();
    }

    //added by jydong
    synchronized void shutdown() {
        if (ackCon.isAlive()) {
            ackCon.shutdownSocketThread();
        }
    }


}
