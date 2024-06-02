package cn.opentp.client.network.handler;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.client.network.ThreadPoolReportService;
import cn.opentp.core.net.OpentpMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OpentpClientHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(OpentpClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof OpentpMessage opentpMessage) {
                Configuration configuration = Configuration._cfg();
                configuration.threadPoolReportService().handle(ctx, opentpMessage);
            } else {
                log.warn("未知消息，丢弃！");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();

            if (state == IdleState.WRITER_IDLE) {
                Configuration configuration = Configuration._cfg();
                ThreadPoolReportService threadPoolReportService = configuration.threadPoolReportService();
                threadPoolReportService.heartbeat(state);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("channel catch exception : ", cause);
        ctx.close();
    }
}
