package cn.opentp.server.rocksdb;

public class Demo {

    public static void main(String[] args) {
        String abc = OpentpRocksDB.get("abc");
        System.out.println(abc);
    }
}
