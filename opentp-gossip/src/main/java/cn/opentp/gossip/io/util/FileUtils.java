package cn.opentp.gossip.io.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public class FileUtils {
    private static Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static void closeQuietly(Closeable c) {
        try {
            if (c != null)
                c.close();
        } catch (Exception e) {
            log.warn("Failed closing {}", c, e);
        }
    }
}
