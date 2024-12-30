package cn.opentp.server.repository.rocksdb;

public interface OpentpRocksDB {

    void set(String key, String value);

    String get(String key);

    boolean exist(String key);

    void delete(String key);

    void close();
}
