package cn.opentp.gossip.node;

import java.io.Serializable;
import java.net.UnknownHostException;

/**
 * GossipNode 摘要
 */
public class GossipNodeDigest implements Serializable, Comparable<GossipNodeDigest> {

    private String host;
    private int port;
    private String nodeId;
    private long heartbeatTime;
    private long version;


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
        this.host = node.getHost();
        this.port = node.getPort();
        this.heartbeatTime = heartbeatTime;
        this.version = version;
        this.nodeId = node.getNodeId();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
                "host='" + host + '\'' +
                ", port=" + port +
                ", nodeId='" + nodeId + '\'' +
                ", heartbeatTime=" + heartbeatTime +
                ", version=" + version +
                '}';
    }
}
