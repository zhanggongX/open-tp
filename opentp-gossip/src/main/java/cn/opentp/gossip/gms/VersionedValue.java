package cn.opentp.gossip.gms;

import cn.opentp.gossip.io.IVersionedSerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class VersionedValue implements Comparable<VersionedValue> {

    public static final IVersionedSerializer<VersionedValue> serializer = new VersionedValueSerializer();

    // this must be a char that cannot be present in any token
    public final static char DELIMITER = ',';
    public final static String DELIMITER_STR = new String(new char[]{DELIMITER});

    // values for ApplicationState.STATUS
    public final static String STATUS_BOOTSTRAPPING = "BOOT";
    public final static String STATUS_NORMAL = "NORMAL";

    public final static String REMOVING_TOKEN = "removing";
    public final static String REMOVED_TOKEN = "removed";

    public final int version;
    public final String value;

    private VersionedValue(String value, int version) {
        assert value != null;
        this.value = value;
        this.version = version;
    }

    private VersionedValue(String value) {
        this(value, VersionGenerator.getNextVersion());
    }

    public int compareTo(VersionedValue value) {
        return this.version - value.version;
    }

    @Override
    public String toString() {
        return "Value(" + value + "," + version + ")";
    }


    private static class VersionedValueSerializer implements IVersionedSerializer<VersionedValue> {

        public void serialize(VersionedValue value, DataOutput dataOutput) throws IOException {
            dataOutput.writeUTF(value.value);
            dataOutput.writeInt(value.version);
        }

        public VersionedValue deserialize(DataInput dataInput) throws IOException {
            String value = dataInput.readUTF();
            int valVersion = dataInput.readInt();
            return new VersionedValue(value, valVersion);
        }

        public long serializedSize(VersionedValue value) {
            throw new UnsupportedOperationException();
        }
    }


    public static class VersionedValueFactory {

        public static final VersionedValueFactory instance = new VersionedValueFactory();

        private VersionedValueFactory() {
        }

        public VersionedValue bootstrapping() {
            return new VersionedValue(VersionedValue.STATUS_BOOTSTRAPPING);
        }

        public VersionedValue normal() {
            return new VersionedValue(VersionedValue.STATUS_NORMAL);
        }

        public VersionedValue removingNonlocal() {
            return new VersionedValue(VersionedValue.REMOVING_TOKEN);
        }

        public VersionedValue removedNonlocal(long expireTime) {
            return new VersionedValue(VersionedValue.REMOVED_TOKEN + VersionedValue.DELIMITER + expireTime);
        }

        public VersionedValue load(double load) {
            return new VersionedValue(String.valueOf(load));
        }

        public VersionedValue weight(int weight) {
            return new VersionedValue(String.valueOf(weight));
        }

        public VersionedValue getVersionedValue(String str) {
            return new VersionedValue(str);
        }
    }
}