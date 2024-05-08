package cn.opentp.gossip.net;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class CompactEndpointSerializationHelper {

    public static void serialize(InetSocketAddress endpoint, DataOutput dos) throws IOException {
        byte[] buf = endpoint.getAddress().getAddress();
        dos.writeByte(buf.length);
        dos.write(buf);
        dos.writeInt(endpoint.getPort());
    }

    public static InetSocketAddress deserialize(DataInput dis) throws IOException {
        byte[] bytes = new byte[dis.readByte()];
        dis.readFully(bytes, 0, bytes.length);
        int port = dis.readInt();
        return new InetSocketAddress(InetAddress.getByAddress(bytes), port);
    }

    public static int serializedSize(InetSocketAddress from) {
        if (from.getAddress() instanceof Inet4Address)
            return 1 + 4 + 4;
        assert from.getAddress() instanceof Inet6Address;
        return 1 + 16 + 4;
    }
}
