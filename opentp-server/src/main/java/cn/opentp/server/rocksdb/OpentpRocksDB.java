package cn.opentp.server.rocksdb;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.nio.charset.StandardCharsets;

public class OpentpRocksDB {

    private static final RocksDB rocksDB;
    private static final String path = "./opentp-rocks";

    static {
        RocksDB.loadLibrary();
        Options options = new Options();
        options.setCreateIfMissing(true);

        try {
            rocksDB = RocksDB.open(options, path);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public static void set(String key, String value) {
        try {
            rocksDB.put(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8));
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        try {
            byte[] bytes = rocksDB.get(key.getBytes(StandardCharsets.UTF_8));
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }
}
