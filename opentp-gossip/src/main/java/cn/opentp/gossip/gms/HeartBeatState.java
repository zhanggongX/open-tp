package cn.opentp.gossip.gms;

import cn.opentp.gossip.io.IVersionedSerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

class HeartBeatState {

    private static IVersionedSerializer<HeartBeatState> serializer;

    static {
        serializer = new HeartBeatStateSerializer();
    }

    private int generation;
    private int version;

    HeartBeatState(int gen) {
        this(gen, 0);
    }

    HeartBeatState(int gen, int ver) {
        generation = gen;
        version = ver;
    }

    public static IVersionedSerializer<HeartBeatState> serializer() {
        return serializer;
    }

    int getGeneration() {
        return generation;
    }

    void updateHeartBeat() {
        version = VersionGenerator.getNextVersion();
    }

    int getHeartBeatVersion() {
        return version;
    }

    void forceNewerGenerationUnsafe() {
        generation += 1;
    }
}

class HeartBeatStateSerializer implements IVersionedSerializer<HeartBeatState> {
    public void serialize(HeartBeatState hbState, DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(hbState.getGeneration());
        dataOutput.writeInt(hbState.getHeartBeatVersion());
    }

    public HeartBeatState deserialize(DataInput dataInput) throws IOException {
        return new HeartBeatState(dataInput.readInt(), dataInput.readInt());
    }

    public long serializedSize(HeartBeatState heartBeatState) {
        throw new UnsupportedOperationException();
    }
}