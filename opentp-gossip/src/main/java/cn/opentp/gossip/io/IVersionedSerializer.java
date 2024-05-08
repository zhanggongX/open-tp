package cn.opentp.gossip.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface IVersionedSerializer<T> {

    void serialize(T t, DataOutput dataOutput) throws IOException;

    T deserialize(DataInput dataInput) throws IOException;

    long serializedSize(T t);
}