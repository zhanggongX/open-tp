package cn.opentp.server.network.receive.message.handler;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.constant.OpentpCoreConstant;
import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.server.network.receive.ThreadPoolReceiveService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReceiveMessageHandler implements MessageHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 处理线程上报信息
     *
     * @param ctx           channelHandler环境
     * @param opentpMessage 消息内容
     */
    @Override
    public void handle(ThreadPoolReceiveService service, ChannelHandlerContext ctx, OpentpMessage opentpMessage) {
        String licenseKey = opentpMessage.getLicenseKey();
        log.debug("接受到信息，认证码：{}", licenseKey);

        String channelLicenseKey = ctx.channel().attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).get();
        if (!licenseKey.equals(channelLicenseKey)) {
            log.warn("licenseKey error");
            ctx.channel().close();
            return;
        }

        if (!service.licenseClientCache().containsKey(licenseKey)) {
            log.warn("licenseKey 异常或已过期，请重新连接");
            ctx.channel().close();
            return;
        }

        // clientInfo 缓存
        ClientInfo clientInfo = service.licenseClientCache().get(licenseKey);
        // 检查缓存的 channel
        Channel channel = service.clientChannelCache().get(clientInfo);
        if (!channel.equals(ctx.channel())) {
            channel.close();
            service.clientChannelCache().put(clientInfo, ctx.channel());
        }

        // 刷新线程池信息
        service.clientThreadPoolStateCache().putIfAbsent(clientInfo, new ConcurrentHashMap<>());
        Map<String, ThreadPoolState> threadPoolStateCache = service.clientThreadPoolStateCache().get(clientInfo);

        List<?> threadPoolStates = (List<?>) opentpMessage.getData();
        for (Object obj : threadPoolStates) {
            ThreadPoolState threadPoolState = (ThreadPoolState) obj;

            threadPoolStateCache.putIfAbsent(threadPoolState.getThreadPoolName(), new ThreadPoolState());
            ThreadPoolState configThreadPoolState = threadPoolStateCache.get(threadPoolState.getThreadPoolName());
            configThreadPoolState.flushState(threadPoolState);

            log.debug("上报线程池信息 : {}", configThreadPoolState);
        }
    }
}
