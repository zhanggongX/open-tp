package cn.opentp.gossip.gms;


import cn.opentp.gossip.io.IVersionedSerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GossipDigestAckMessage {

    private static IVersionedSerializer<GossipDigestAckMessage> _serializer;

    static {
        _serializer = new GossipDigestAckMessageSerializer();
    }

    List<GossipDigest> gDigestList_ = new ArrayList<GossipDigest>();
    Map<InetSocketAddress, EndpointState> epStateMap_ = new HashMap<InetSocketAddress, EndpointState>();

    static IVersionedSerializer<GossipDigestAckMessage> serializer() {
        return _serializer;
    }

    GossipDigestAckMessage(List<GossipDigest> gDigestList, Map<InetSocketAddress, EndpointState> epStateMap) {
        gDigestList_ = gDigestList;
        epStateMap_ = epStateMap;
    }

    List<GossipDigest> getGossipDigestList() {
        return gDigestList_;
    }

    Map<InetSocketAddress, EndpointState> getEndpointStateMap() {
        return epStateMap_;
    }
}

class GossipDigestAckMessageSerializer implements IVersionedSerializer<GossipDigestAckMessage> {
    public void serialize(GossipDigestAckMessage gDigestAckMessage, DataOutput dos) throws IOException {
        GossipDigestSerializationHelper.serialize(gDigestAckMessage.gDigestList_, dos);
        dos.writeBoolean(true); // 0.6 compatibility
        EndpointStatesSerializationHelper.serialize(gDigestAckMessage.epStateMap_, dos);
    }

    public GossipDigestAckMessage deserialize(DataInput dis) throws IOException {
        List<GossipDigest> gDigestList = GossipDigestSerializationHelper.deserialize(dis);
        dis.readBoolean(); // 0.6 compatibility
        Map<InetSocketAddress, EndpointState> epStateMap = EndpointStatesSerializationHelper.deserialize(dis);
        return new GossipDigestAckMessage(gDigestList, epStateMap);
    }

    public long serializedSize(GossipDigestAckMessage gossipDigestAckMessage) {
        throw new UnsupportedOperationException();
    }
}
