package cn.opentp.gossip.node;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * GossipNode 摘要
 */
public class GossipNodeDigest implements Serializable, Comparable<GossipNodeDigest> {

    private String nodeId;
    private long heartbeatTime;
    private long version;
    private InetSocketAddress socketAddress;

    @Override
    public int compareTo(GossipNodeDigest o) {
        if (heartbeatTime != o.heartbeatTime) {
            return (int) (heartbeatTime - o.heartbeatTime);
        }
        return (int) (version - o.version);
    }

    public GossipNodeDigest() {
    }

    public GossipNodeDigest(GossipNode node, long heartbeatTime, long version) throws UnknownHostException {
        this.socketAddress = new InetSocketAddress(InetAddress.getByName(node.getHost()), node.getPort());
        this.heartbeatTime = heartbeatTime;
        this.version = version;
        this.nodeId = node.getNodeId();
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(InetSocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public long getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "GossipNodeDigest{" +
                "socketAddress=" + socketAddress +
                ", heartbeatTime=" + heartbeatTime +
                ", version=" + version +
                ", nodeId='" + nodeId + '\'' +
                '}';
    }
}
