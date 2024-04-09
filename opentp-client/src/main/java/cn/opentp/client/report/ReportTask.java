package cn.opentp.client.report;

import cn.opentp.client.context.OpentpContext;
import cn.opentp.client.net.NettyClient;
import cn.opentp.core.tp.ThreadPoolWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ReportTask extends TimerTask {
    private static final Logger log = LoggerFactory.getLogger(ReportTask.class);

    @Override
    public void run() {
        Map<String, ThreadPoolWrapper> allTps = OpentpContext.allTps();
        for (Map.Entry<String, ThreadPoolWrapper> entry : allTps.entrySet()) {
            entry.getValue().flush();
            entry.getValue().setThreadName(entry.getKey());
            NettyClient.send(entry.getValue());
        }
    }

    public void startReport() {
        log.debug("netty client started");
        new Timer().schedule(new ReportTask(), 0, 1000);
    }
}
