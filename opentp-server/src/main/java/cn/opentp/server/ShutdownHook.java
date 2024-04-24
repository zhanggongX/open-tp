package cn.opentp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShutdownHook extends Thread {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final List<Closeable> closeableList = new ArrayList<>();

    public void add(Closeable c) {
        closeableList.add(c);
    }

    @Override
    public void run() {
        if (!closeableList.isEmpty()) {
            for (Closeable closeable : closeableList) {
                try {
                    log.debug("开始关闭 {}", closeable);
                    closeable.close();
                    log.debug("完成关闭 {}", closeable);
                } catch (IOException e) {
                    log.error("{} close fail, ", closeable, e);
                }
            }
        }
    }
}
