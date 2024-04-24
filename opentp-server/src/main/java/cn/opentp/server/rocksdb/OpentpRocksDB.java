package cn.opentp.server.rocksdb;

import org.apache.logging.log4j.util.Strings;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class OpentpRocksDB {

    private static final RocksDB rocksDB;
    private static final String path = "../opentp-rocks";
    private final static Logger log = LoggerFactory.getLogger(OpentpRocksDB.class);

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
            if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
                return;
            }
            rocksDB.put(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8));
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        try {
            byte[] bytes = rocksDB.get(key.getBytes(StandardCharsets.UTF_8));
            if (bytes == null) return Strings.EMPTY;

            return new String(bytes, StandardCharsets.UTF_8);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close() {
        try {
            rocksDB.close();
        } catch (Exception e) {
            log.error("close error : ", e);
        }
    }
}
