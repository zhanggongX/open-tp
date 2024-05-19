package cn.opentp.server.network.report.handler;

import cn.opentp.core.net.OpentpMessage;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.network.report.ThreadPoolReportService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(ReportServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof OpentpMessage opentpMessage) {
                ThreadPoolReportService threadPoolReportService = OpentpApp.instance().reportService();
                // 处理消息
                threadPoolReportService.handle(ctx, opentpMessage);
            } else {
                log.warn("未知消息类型，丢弃！");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.warn("心跳超时，关闭连接！");
                ThreadPoolReportService threadPoolReportService = OpentpApp.instance().reportService();
                threadPoolReportService.clientClose(ctx.channel());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("channel 捕获异常 : ", cause);
    }
}
