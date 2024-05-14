package cn.opentp.server.report.handler;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.auth.License;
import cn.opentp.core.auth.ServerInfo;
import cn.opentp.core.constant.OpentpCoreConstant;
import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageConstant;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.core.net.serializer.SerializerTypeEnum;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.server.auth.LicenseKeyFactory;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.constant.OpentpServerConstant;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ReportServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(ReportServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof OpentpMessage opentpMessage) {
                channelRead0(ctx, opentpMessage);
            } else {
                log.warn("未知消息类型，丢弃！");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 处理 OpentpMessage
     *
     * @param ctx           channel 上下文
     * @param opentpMessage 信息
     */
    private void channelRead0(ChannelHandlerContext ctx, OpentpMessage opentpMessage) {

        OpentpMessageTypeEnum opentpMessageTypeEnum = OpentpMessageTypeEnum.parse(opentpMessage.getMessageType());
        OpentpApp configuration = null;

        switch (Objects.requireNonNull(opentpMessageTypeEnum)) {
            case HEART_PING:
                log.info("接收心跳信息： {} 应答: {}", opentpMessage.getData(), OpentpMessageConstant.HEARD_PONG);
                break;
            case THREAD_POOL_EXPORT:
                channelReadThreadPoolExport(ctx, opentpMessage);
                break;
            case AUTHENTICATION_REQ:
                channelReadAuthReq(ctx, opentpMessage);
                break;
            default:
                log.warn("未知的消息类型，不处理！");
        }
    }

    private void channelReadAuthReq(ChannelHandlerContext ctx, OpentpMessage opentpMessage) {
        ClientInfo clientInfo = (ClientInfo) opentpMessage.getData();

        log.debug("有新认证到来，appKey: {}, appSecret: {}, host: {}, instance: {}", clientInfo.getAppKey(), clientInfo.getAppSecret(), clientInfo.getHost(), clientInfo.getInstance());
        // todo 认证消息动态
        if (clientInfo.getAppKey() == null || !clientInfo.getAppKey().equals(OpentpServerConstant.ADMIN_DEFAULT_APP)) {
            log.warn("新认证到来，未知的 appId : {}", clientInfo.getAppKey());
            ctx.channel().close();
            return;
        }
        if (clientInfo.getAppSecret() == null || !clientInfo.getAppSecret().equals(OpentpServerConstant.ADMIN_DEFAULT_SECRET)) {
            log.warn("新认证到来, appId : {}, appSecret error ", clientInfo.getAppKey());
            ctx.channel().close();
            return;
        }

        String newLicenseKey = LicenseKeyFactory.get();
        log.debug("新链接认证成功：返回 licenseKey : {}", newLicenseKey);

        // 设置 licenseKey
        ctx.channel().attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).set(newLicenseKey);
        OpentpApp opentpApp = OpentpApp.instance();
        // 记录 licenseKey <-> clientInfo
        opentpApp.licenseKeyClientCache().putIfAbsent(newLicenseKey, clientInfo);
        // 记录 客户端信息 <-> 网络连接
        opentpApp.clientChannelCache().put(clientInfo, ctx.channel());
        opentpApp.clientKeyChannelCache().put(clientInfo.clientKey(), ctx.channel());

        ServerInfo thisServerInfo = new ServerInfo();
        List<ClientInfo> clientInfos = opentpApp.clusterServerInfoCache().getOrDefault(thisServerInfo, new ArrayList<>());
        clientInfos.add(clientInfo);
        opentpApp.clusterServerInfoCache().put(thisServerInfo, clientInfos);

        OpentpMessage opentpMessageRes = OpentpCoreConstant.OPENTP_MSG_PROTO.clone();
        OpentpMessage
                .builder()
                .messageType(OpentpMessageTypeEnum.AUTHENTICATION_RES.getCode())
                .serializerType(SerializerTypeEnum.Kryo.getType())
                .data(new License(newLicenseKey))
                .traceId(opentpMessage.getTraceId())
                .buildTo(opentpMessageRes);

        // 返回 licenseKey
        ctx.channel().writeAndFlush(opentpMessageRes);
    }

    private void channelReadThreadPoolExport(ChannelHandlerContext ctx, OpentpMessage opentpMessage) {
        String licenseKey = opentpMessage.getLicenseKey();
        log.debug("接受到信息，认证码：{}", licenseKey);

        String channelLicenseKey = ctx.channel().attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).get();
        if (!licenseKey.equals(channelLicenseKey)) {
            log.warn("licenseKey error");
            ctx.channel().close();
            return;
        }

        OpentpApp opentpApp = OpentpApp.instance();
        if (!opentpApp.licenseKeyClientCache().containsKey(licenseKey)) {
            log.warn("licenseKey 异常或已过期，请重新连接");
            ctx.channel().close();
            return;
        }

        // clientInfo 缓存
        ClientInfo clientInfo = opentpApp.licenseKeyClientCache().get(licenseKey);
        Map<ClientInfo, Map<String, ThreadPoolState>> clientThreadPoolStatesCache = opentpApp.clientThreadPoolStatesCache();
        clientThreadPoolStatesCache.putIfAbsent(clientInfo, new ConcurrentHashMap<>());
        Map<String, ThreadPoolState> threadPoolStateCache = clientThreadPoolStatesCache.get(clientInfo);

        // clientInfoKey 缓存
        Map<String, Map<String, ThreadPoolState>> clientKeyThreadPoolStatesCache = opentpApp.clientKeyThreadPoolStatesCache();
        clientKeyThreadPoolStatesCache.putIfAbsent(clientInfo.clientKey(), new ConcurrentHashMap<>());
        Map<String, ThreadPoolState> keyThreadPoolStateCache = clientKeyThreadPoolStatesCache.get(clientInfo.clientKey());


        List<?> threadPoolStates = (List<?>) opentpMessage.getData();
        for (Object obj : threadPoolStates) {
            ThreadPoolState threadPoolState = (ThreadPoolState) obj;

            threadPoolStateCache.putIfAbsent(threadPoolState.getThreadPoolName(), new ThreadPoolState());
            ThreadPoolState configThreadPoolState = threadPoolStateCache.get(threadPoolState.getThreadPoolName());
            configThreadPoolState.flushState(threadPoolState);

            // clientInfoKey 缓存
            keyThreadPoolStateCache.put(threadPoolState.getThreadPoolName(), configThreadPoolState);

            log.debug("上报线程池信息 : {}", configThreadPoolState.toString());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.warn("心跳超时，关闭连接！");
                removeChannelInfo(ctx.channel());
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("channel 捕获异常 : ", cause);
        removeChannelInfo(ctx.channel());
        ctx.close();
    }

    private void removeChannelInfo(Channel channel) {
        String licenseKey = channel.attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).get();

        OpentpApp opentpApp = OpentpApp.instance();
        ClientInfo clientInfo = opentpApp.licenseKeyClientCache().get(licenseKey);
        opentpApp.clientChannelCache().remove(clientInfo);
        opentpApp.clientKeyChannelCache().remove(clientInfo.clientKey());
        opentpApp.clientThreadPoolStatesCache().remove(clientInfo);
        opentpApp.clientKeyThreadPoolStatesCache().remove(clientInfo.clientKey());
    }
}
