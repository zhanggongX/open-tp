package cn.opentp.client.report;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.client.net.NettyClient;
import cn.opentp.core.tp.ThreadPoolContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ReportTask extends TimerTask {
    private static final Logger log = LoggerFactory.getLogger(ReportTask.class);

    @Override
    public void run() {
        Map<String, ThreadPoolContext> threadPoolContextCache = Configuration.configuration().threadPoolContextCache();
        for (Map.Entry<String, ThreadPoolContext> threadPoolContextEntry : threadPoolContextCache.entrySet()) {
            threadPoolContextEntry.getValue().flush();
            threadPoolContextEntry.getValue().setThreadName(threadPoolContextEntry.getKey());
//            NettyClient.send(threadPoolContextEntry.getValue());
        }
    }

    public static void startReport() {
        log.debug("netty client started");
        new Timer().schedule(new ReportTask(), 0, 1000);
    }
}
