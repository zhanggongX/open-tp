package cn.opentp.server.repository.rocksdb;

import com.google.inject.Singleton;
import org.apache.logging.log4j.util.Strings;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;

@Singleton
public class OpentpRocksDB implements Closeable {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RocksDB rocksDB;

    private OpentpRocksDB() {
        RocksDB.loadLibrary();
        Options options = new Options();
        options.setCreateIfMissing(true);

        try {
            // todo config
            String path = "../opentp-rocks";
            rocksDB = RocksDB.open(options, path);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(String key, String value) {
        try {
            if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
                return;
            }
            rocksDB.put(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8));
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public String get(String key) {
        try {
            byte[] bytes = rocksDB.get(key.getBytes(StandardCharsets.UTF_8));
            if (bytes == null) return Strings.EMPTY;

            return new String(bytes, StandardCharsets.UTF_8);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exist(String key) {
        try {
            return rocksDB.get(key.getBytes(StandardCharsets.UTF_8)) != null;
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String key) {
        try {
            rocksDB.delete(key.getBytes(StandardCharsets.UTF_8));
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            rocksDB.close();
        } catch (Exception e) {
            log.error("close error : ", e);
        }
    }
}
