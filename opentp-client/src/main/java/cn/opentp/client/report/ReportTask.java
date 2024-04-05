package cn.opentp.client.report;

import cn.opentp.client.context.OpentpContext;
import cn.opentp.client.net.NettyClient;
import cn.opentp.core.tp.ThreadPoolWrapper;
import cn.opentp.core.tp.serializer.Serializer;
import cn.opentp.core.tp.serializer.kryo.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReportTask extends TimerTask {
    private static final Logger log = LoggerFactory.getLogger(ReportTask.class);

    @Override
    public void run() {
        Serializer serializer = new KryoSerializer();
        List<ThreadPoolWrapper> threadPoolWrappers = OpentpContext.allTps();
        for(ThreadPoolWrapper tpw : threadPoolWrappers){
            tpw.flush();
            byte[] serialize = serializer.serialize(tpw);
            NettyClient.send(serialize);
        }
//        ByteBuf byteBuf = Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8);
//        NettyClient.send(byteBuf);
    }

    public void startReport(){
        log.debug("netty client started");
        new Timer().schedule(new ReportTask(), 0, 1000);
    }
}
