package cn.opentp.gossip.gms;

import cn.opentp.gossip.io.IVersionedSerializer;
import cn.opentp.gossip.net.CompactEndpointSerializationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GossipDigestSynMessage {

    private static IVersionedSerializer<GossipDigestSynMessage> _serializer;

    static {
        _serializer = new GossipDigestSynMessageSerializer();
    }

    String clusterId_;
    List<GossipDigest> gDigests_ = new ArrayList<GossipDigest>();

    public static IVersionedSerializer<GossipDigestSynMessage> serializer() {
        return _serializer;
    }

    public GossipDigestSynMessage(String clusterId, List<GossipDigest> gDigests) {
        clusterId_ = clusterId;
        gDigests_ = gDigests;
    }

    List<GossipDigest> getGossipDigests() {
        return gDigests_;
    }
}

class GossipDigestSerializationHelper {

    private static Logger logger_ = LoggerFactory.getLogger(GossipDigestSerializationHelper.class);

    static void serialize(List<GossipDigest> gDigestList, DataOutput dos) throws IOException {
        dos.writeInt(gDigestList.size());
        for (GossipDigest gDigest : gDigestList) {
            GossipDigest.serializer().serialize(gDigest, dos);
        }
    }

    static List<GossipDigest> deserialize(DataInput dis) throws IOException {
        int size = dis.readInt();
        List<GossipDigest> gDigests = new ArrayList<GossipDigest>(size);

        for (int i = 0; i < size; ++i) {
            gDigests.add(GossipDigest.serializer().deserialize(dis));
        }
        return gDigests;
    }
}

class EndpointStatesSerializationHelper {
    private static final Logger logger_ = LoggerFactory.getLogger(EndpointStatesSerializationHelper.class);

    static void serialize(Map<InetSocketAddress, EndpointState> epStateMap, DataOutput dos) throws IOException {
        dos.writeInt(epStateMap.size());
        for (Map.Entry<InetSocketAddress, EndpointState> entry : epStateMap.entrySet()) {
            InetSocketAddress ep = entry.getKey();
            CompactEndpointSerializationHelper.serialize(ep, dos);
            EndpointState.serializer().serialize(entry.getValue(), dos);
        }
    }

    static Map<InetSocketAddress, EndpointState> deserialize(DataInput dis) throws IOException {
        int size = dis.readInt();
        Map<InetSocketAddress, EndpointState> epStateMap = new HashMap<InetSocketAddress, EndpointState>(size);

        for (int i = 0; i < size; ++i) {
            InetSocketAddress ep = CompactEndpointSerializationHelper.deserialize(dis);
            EndpointState epState = EndpointState.serializer().deserialize(dis);
            epStateMap.put(ep, epState);
        }
        return epStateMap;
    }
}

class GossipDigestSynMessageSerializer implements IVersionedSerializer<GossipDigestSynMessage> {
    public void serialize(GossipDigestSynMessage gDigestSynMessage, DataOutput dos) throws IOException {
        dos.writeUTF(gDigestSynMessage.clusterId_);
        GossipDigestSerializationHelper.serialize(gDigestSynMessage.gDigests_, dos);
    }

    public GossipDigestSynMessage deserialize(DataInput dis) throws IOException {
        String clusterId = dis.readUTF();
        List<GossipDigest> gDigests = GossipDigestSerializationHelper.deserialize(dis);
        return new GossipDigestSynMessage(clusterId, gDigests);
    }

    public long serializedSize(GossipDigestSynMessage gossipDigestSynMessage) {
        throw new UnsupportedOperationException();
    }
}

