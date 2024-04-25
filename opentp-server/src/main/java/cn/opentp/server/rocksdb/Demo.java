package cn.opentp.server.rocksdb;

public class Demo {

    public static void main(String[] args) {
        String abc = OpentpRocksDB.rocksDB().get("abc");
        System.out.println(abc);

        String aaa = OpentpRocksDB.rocksDB().get("aaa");
        System.out.println(1);
    }
}
