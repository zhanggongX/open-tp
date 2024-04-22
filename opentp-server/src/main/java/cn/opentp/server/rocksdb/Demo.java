package cn.opentp.server.rocksdb;

public class Demo {

    public static void main(String[] args) {
        String abc = OpentpRocksDB.get("abc");
        System.out.println(abc);

        String aaa = OpentpRocksDB.get("aaa");
        System.out.println(1);
    }
}
