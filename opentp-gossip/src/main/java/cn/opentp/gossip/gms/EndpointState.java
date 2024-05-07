package cn.opentp.gossip.gms;

import cn.opentp.gossip.enums.ApplicationStateEnum;
import cn.opentp.gossip.io.IVersionedSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EndpointState {

    protected static Logger logger = LoggerFactory.getLogger(EndpointState.class);

    private final static IVersionedSerializer<EndpointState> serializer = new EndpointStateSerializer();

    private volatile HeartBeatState hbState;
    final Map<ApplicationStateEnum, VersionedValue> applicationState = new NonBlockingHashMap<ApplicationState, VersionedValue>();

    /* fields below do not get serialized */
    private volatile long updateTimestamp;
    private volatile boolean isAlive;

    public static IVersionedSerializer<EndpointState> serializer() {
        return serializer;
    }

    EndpointState(HeartBeatState initialHbState) {
        hbState = initialHbState;
        updateTimestamp = System.currentTimeMillis();
        isAlive = true;
    }

    HeartBeatState getHeartBeatState() {
        return hbState;
    }

    void setHeartBeatState(HeartBeatState newHbState) {
        updateTimestamp();
        hbState = newHbState;
    }

    public VersionedValue getApplicationState(ApplicationState key) {
        return applicationState.get(key);
    }

    public Collection<VersionedValue> getApplicationStateMapValues() {
        return applicationState.values();
    }

    public Set<Map.Entry<ApplicationState, VersionedValue>> getApplicationStateMapEntrySet() {
        return applicationState.entrySet();
    }

    void addApplicationState(ApplicationStateEnum key, VersionedValue value) {
        applicationState.put(key, value);
    }

    /* getters and setters */
    public long getUpdateTimestamp() {
        return updateTimestamp;
    }

    void updateTimestamp() {
        updateTimestamp = System.currentTimeMillis();
    }

    public boolean isAlive() {
        return isAlive;
    }

    void markAlive() {
        isAlive = true;
    }

    void markDead() {
        isAlive = false;
    }
}

class EndpointStateSerializer implements IVersionedSerializer<EndpointState> {
    private static Logger logger = LoggerFactory.getLogger(EndpointStateSerializer.class);

    public void serialize(EndpointState epState, DataOutput dos) throws IOException {
        /* serialize the HeartBeatState */
        HeartBeatState hbState = epState.getHeartBeatState();
        HeartBeatState.serializer().serialize(hbState, dos);

        /* serialize the map of ApplicationState objects */
        int size = epState.applicationState.size();
        dos.writeInt(size);
        for (Map.Entry<ApplicationState, VersionedValue> entry : epState.applicationState.entrySet()) {
            VersionedValue value = entry.getValue();
            dos.writeInt(entry.getKey().ordinal());
            VersionedValue.serializer.serialize(value, dos);
        }
    }

    public EndpointState deserialize(DataInput dis) throws IOException {
        HeartBeatState hbState = HeartBeatState.serializer().deserialize(dis);
        EndpointState epState = new EndpointState(hbState);

        int appStateSize = dis.readInt();
        for (int i = 0; i < appStateSize; ++i) {
            int key = dis.readInt();
            VersionedValue value = VersionedValue.serializer.deserialize(dis);
            epState.addApplicationState(Gossiper.STATES[key], value);
        }
        return epState;
    }

    public long serializedSize(EndpointState endpointState) {
        throw new UnsupportedOperationException();
    }
}