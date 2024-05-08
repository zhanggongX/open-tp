package cn.opentp.gossip.net;


import cn.opentp.gossip.io.IVersionedSerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Header {
    private static IVersionedSerializer<Header> serializer_;

    static {
        serializer_ = new HeaderSerializer();
    }

    public static IVersionedSerializer<Header> serializer() {
        return serializer_;
    }

    // "from" is the ultimate origin of this request (the coordinator), which in a multi-DC setup
    // is not necessarily the same as the node that forwards us the request (see StorageProxy.sendMessages
    // and RowMutationVerbHandler.forwardToLocalNodes)
    private final InetSocketAddress from_;
    private final MessageVerb.Verb verb_;

    Header(InetSocketAddress from, MessageVerb.Verb verb) {
        assert from != null;
        assert verb != null;

        from_ = from;
        verb_ = verb;
    }


    InetSocketAddress getFrom() {
        return from_;
    }

    MessageVerb.Verb getVerb() {
        return verb_;
    }

    public int serializedSize() {
        int size = 0;
        size += CompactEndpointSerializationHelper.serializedSize(getFrom());
        size += 4;
        return size;
    }
}

class HeaderSerializer implements IVersionedSerializer<Header> {
    public void serialize(Header t, DataOutput dos) throws IOException {
        CompactEndpointSerializationHelper.serialize(t.getFrom(), dos);
        dos.writeInt(t.getVerb().ordinal());
    }

    public Header deserialize(DataInput dis) throws IOException {
        InetSocketAddress from = CompactEndpointSerializationHelper.deserialize(dis);
        int verbOrdinal = dis.readInt();
        return new Header(from, MessageVerb.VERBS[verbOrdinal]);
    }

    public long serializedSize(Header header) {
        throw new UnsupportedOperationException();
    }
}


