package cn.opentp.gossip.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface IVersionedSerializer<T> {

    public void serialize(T t, DataOutput dataOutput) throws IOException;

    public T deserialize(DataInput dataInput) throws IOException;

    public long serializedSize(T t);
}