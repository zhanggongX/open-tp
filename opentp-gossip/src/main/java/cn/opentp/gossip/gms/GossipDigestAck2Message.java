package cn.opentp.gossip.gms;

import cn.opentp.gossip.io.IVersionedSerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

class GossipDigestAck2Message {

    private static IVersionedSerializer<GossipDigestAck2Message> _serializer;

    static {
        _serializer = new GossipDigestAck2MessageSerializer();
    }

    Map<InetSocketAddress, EndpointState> epStateMap_ = new HashMap<InetSocketAddress, EndpointState>();

    public static IVersionedSerializer<GossipDigestAck2Message> serializer() {
        return _serializer;
    }

    GossipDigestAck2Message(Map<InetSocketAddress, EndpointState> epStateMap) {
        epStateMap_ = epStateMap;
    }

    Map<InetSocketAddress, EndpointState> getEndpointStateMap() {
        return epStateMap_;
    }
}

class GossipDigestAck2MessageSerializer implements IVersionedSerializer<GossipDigestAck2Message> {
    public void serialize(GossipDigestAck2Message gDigestAck2Message, DataOutput dos) throws IOException {
        /* Use the EndpointState */
        EndpointStatesSerializationHelper.serialize(gDigestAck2Message.epStateMap_, dos);
    }

    public GossipDigestAck2Message deserialize(DataInput dis) throws IOException {
        Map<InetSocketAddress, EndpointState> epStateMap = EndpointStatesSerializationHelper.deserialize(dis);
        return new GossipDigestAck2Message(epStateMap);
    }

    public long serializedSize(GossipDigestAck2Message gossipDigestAck2Message) {
        throw new UnsupportedOperationException();
    }
}

