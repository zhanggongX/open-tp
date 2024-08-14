package cn.opentp.server.network.receive.message.handler;

import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.server.network.receive.ThreadPoolReceiveService;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

public interface MessageHandler {

    Map<OpentpMessageTypeEnum, MessageHandler> HANDLER_MAP = new HashMap<>();

    /**
     * 处理 opentp message 信息内容
     *
     * @param service       网络服务
     * @param ctx           channelHandler 上下文
     * @param opentpMessage opentp message 内容
     */
    void handle(ThreadPoolReceiveService service, ChannelHandlerContext ctx, OpentpMessage opentpMessage);
}
