package cn.opentp.client.core.report;

import cn.opentp.client.core.net.NettyClient;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class ReportTask extends TimerTask {
    private static final Logger log = LoggerFactory.getLogger(ReportTask.class);

    @Override
    public void run() {
        ByteBuf byteBuf = Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8);
        NettyClient.send(byteBuf);
    }

    public void startReport(){
        log.debug("netty client started");
        new Timer().schedule(new ReportTask(), 0, 1000);
    }
}
