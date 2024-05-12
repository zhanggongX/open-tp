package cn.opentp.gossip.message;

import cn.opentp.gossip.node.GossipNodeDigest;

import java.io.Serializable;
import java.util.List;

public class SyncMessage implements Serializable {
    private String cluster;
    private List<GossipNodeDigest> digestList;

    public SyncMessage() {
    }

    public SyncMessage(String cluster, List<GossipNodeDigest> digestList) {
        this.cluster = cluster;
        this.digestList = digestList;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public List<GossipNodeDigest> getDigestList() {
        return digestList;
    }

    public void setDigestList(List<GossipNodeDigest> digestList) {
        this.digestList = digestList;
    }

    @Override
    public String toString() {
        return "GossipDigestSyncMessage{" +
                "cluster='" + cluster + '\'' +
                ", digestList=" + digestList +
                '}';
    }

}
