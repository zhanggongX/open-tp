package cn.opentp.server.rocksdb;

public class Demo {

    public static void main(String[] args) {
        String abc = OpentpRocksDB.rocksDB().get("abc");
        System.out.println(abc);

        OpentpRocksDB opentpRocksDB = OpentpRocksDB.rocksDB();
        String aaa = opentpRocksDB.get("aaa");
        System.out.println(1);
    }
}
