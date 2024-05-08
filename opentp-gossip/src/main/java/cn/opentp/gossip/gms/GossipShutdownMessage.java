package cn.opentp.gossip.gms;

import cn.opentp.gossip.io.IVersionedSerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


class GossipShutdownMessage {
    private static final IVersionedSerializer<GossipShutdownMessage> serializer;

    static {
        serializer = new GossipShutdownMessageSerializer();
    }

    static IVersionedSerializer<GossipShutdownMessage> serializer() {
        return serializer;
    }

    GossipShutdownMessage() {
    }
}

class GossipShutdownMessageSerializer implements IVersionedSerializer<GossipShutdownMessage> {
    public void serialize(GossipShutdownMessage gShutdownMessage, DataOutput dos) throws IOException {
    }

    public GossipShutdownMessage deserialize(DataInput dis) throws IOException {
        return new GossipShutdownMessage();
    }

    public long serializedSize(GossipShutdownMessage gossipShutdownMessage) {
        throw new UnsupportedOperationException();
    }
}