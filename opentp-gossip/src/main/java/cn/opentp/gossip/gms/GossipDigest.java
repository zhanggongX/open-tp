package cn.opentp.gossip.gms;

import cn.opentp.gossip.io.IVersionedSerializer;
import cn.opentp.gossip.net.CompactEndpointSerializationHelper;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetSocketAddress;

public class GossipDigest implements Comparable<GossipDigest> {

    private static IVersionedSerializer<GossipDigest> serializer;

    static {
        serializer = new GossipDigestSerializer();
    }

    InetSocketAddress endpoint;
    int generation;
    int maxVersion;

    public static IVersionedSerializer<GossipDigest> serializer() {
        return serializer;
    }

    GossipDigest(InetSocketAddress ep, int gen, int version) {
        endpoint = ep;
        generation = gen;
        maxVersion = version;
    }

    InetSocketAddress getEndpoint() {
        return endpoint;
    }

    int getGeneration() {
        return generation;
    }

    int getMaxVersion() {
        return maxVersion;
    }

    public int compareTo(GossipDigest gDigest) {
        if (generation != gDigest.generation)
            return (generation - gDigest.generation);
        return (maxVersion - gDigest.maxVersion);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(endpoint);
        sb.append(":");
        sb.append(generation);
        sb.append(":");
        sb.append(maxVersion);
        return sb.toString();
    }
}


class GossipDigestSerializer implements IVersionedSerializer<GossipDigest> {
    public void serialize(GossipDigest gDigest, DataOutput dos) throws IOException {
        CompactEndpointSerializationHelper.serialize(gDigest.endpoint, dos);
        dos.writeInt(gDigest.generation);
        dos.writeInt(gDigest.maxVersion);
    }

    public GossipDigest deserialize(DataInput dis) throws IOException {
        InetSocketAddress endpoint = CompactEndpointSerializationHelper.deserialize(dis);
        int generation = dis.readInt();
        int maxVersion = dis.readInt();
        return new GossipDigest(endpoint, generation, maxVersion);
    }

    public long serializedSize(GossipDigest gossipDigest) {
        throw new UnsupportedOperationException();
    }
}
