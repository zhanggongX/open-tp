package cn.opentp.server.network.receive;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.auth.License;
import cn.opentp.core.constant.OpentpCoreConstant;
import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageConstant;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.core.net.codec.OpentpMessageDecoder;
import cn.opentp.core.net.codec.OpentpMessageEncoder;
import cn.opentp.core.net.serializer.SerializerTypeEnum;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.core.util.JacksonUtil;
import cn.opentp.core.util.MessageTraceIdUtil;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.auth.LicenseKeyFactory;
import cn.opentp.server.constant.OpentpServerConstant;
import cn.opentp.server.network.receive.netty.handler.ReceiveServiceNettyHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ThreadPoolReceiveService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // key = appId, value = 所有连接上来的客户端
    private final Map<String, List<ClientInfo>> appKeyClientCache = new ConcurrentHashMap<>();

    // key = licenseKey, value = 客户端信息
    private final Map<String, ClientInfo> licenseClientCache = new ConcurrentHashMap<>();
    // key = 客户端信息, value = channel
    private final Map<ClientInfo, Channel> clientChannelCache = new ConcurrentHashMap<>();
    // key = 客户端信息, value = <key = threadKey, value = threadPoolSate>
    private final Map<ClientInfo, Map<String, ThreadPoolState>> clientThreadPoolStateCache = new ConcurrentHashMap<>();

    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
    private final NioEventLoopGroup workGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);

    public void start(String host, int port) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // 20s 收不到心跳，就认为该 channel 断开。
                        socketChannel.pipeline().addLast(new IdleStateHandler(15, 0, 0));
                        socketChannel.pipeline().addLast(new OpentpMessageEncoder());
                        socketChannel.pipeline().addLast(new OpentpMessageDecoder());
                        socketChannel.pipeline().addLast(new ReceiveServiceNettyHandler());
                    }
                });

        serverBootstrap.bind(host, port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("thread pool report service start bind on {}:{}", host, port);
                } else {
                    log.error("thread pool report service start error: ", future.cause());
                }
            }
        });
    }

    /**
     * 处理 OpentpMessage
     *
     * @param ctx           channel 上下文
     * @param opentpMessage 信息
     */
    public void handle(ChannelHandlerContext ctx, OpentpMessage opentpMessage) {
        OpentpMessageTypeEnum opentpMessageTypeEnum = OpentpMessageTypeEnum.parse(opentpMessage.getMessageType());

        switch (Objects.requireNonNull(opentpMessageTypeEnum)) {
            case HEART_PING:
                log.info("接收心跳信息： {} 应答: {}", opentpMessage.getData(), OpentpMessageConstant.HEARD_PONG);
                break;
            case AUTHENTICATION_REQ:
                channelReadAuthReq(ctx, opentpMessage);
                break;
            case THREAD_POOL_EXPORT:
                channelReadThreadPoolExport(ctx, opentpMessage);
                break;
            default:
                log.warn("未知的消息类型，不处理！");
        }
    }

    /**
     * 处理客户端连接信息
     *
     * @param ctx           channelHandler环境
     * @param opentpMessage 消息内容
     */
    private void channelReadAuthReq(ChannelHandlerContext ctx, OpentpMessage opentpMessage) {
        ClientInfo clientInfo = (ClientInfo) opentpMessage.getData();
        clientInfo.setServerInfo(OpentpApp.instance().selfInfo());

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

        // 记录 appKey <-> 客户端信息
        appKeyClientCache.putIfAbsent(OpentpServerConstant.ADMIN_DEFAULT_APP, new ArrayList<>());
        appKeyClientCache.get(OpentpServerConstant.ADMIN_DEFAULT_APP).add(clientInfo);

        // 记录 licenseKey <-> 客户端信息
        licenseClientCache.put(newLicenseKey, clientInfo);
        // 记录 客户端信息 <-> 网络连接
        clientChannelCache.put(clientInfo, ctx.channel());

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

    /**
     * 处理线程上报信息
     *
     * @param ctx           channelHandler环境
     * @param opentpMessage 消息内容
     */
    private void channelReadThreadPoolExport(ChannelHandlerContext ctx, OpentpMessage opentpMessage) {
        String licenseKey = opentpMessage.getLicenseKey();
        log.debug("接受到信息，认证码：{}", licenseKey);

        String channelLicenseKey = ctx.channel().attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).get();
        if (!licenseKey.equals(channelLicenseKey)) {
            log.warn("licenseKey error");
            ctx.channel().close();
            return;
        }

        if (!licenseClientCache.containsKey(licenseKey)) {
            log.warn("licenseKey 异常或已过期，请重新连接");
            ctx.channel().close();
            return;
        }

        // clientInfo 缓存
        ClientInfo clientInfo = licenseClientCache.get(licenseKey);
        // 检查缓存的 channel
        Channel channel = clientChannelCache.get(clientInfo);
        if (!channel.equals(ctx.channel())) {
            channel.close();
            clientChannelCache.put(clientInfo, ctx.channel());
        }

        // 刷新线程池信息
        clientThreadPoolStateCache.putIfAbsent(clientInfo, new ConcurrentHashMap<>());
        Map<String, ThreadPoolState> threadPoolStateCache = clientThreadPoolStateCache.get(clientInfo);

        List<?> threadPoolStates = (List<?>) opentpMessage.getData();
        for (Object obj : threadPoolStates) {
            ThreadPoolState threadPoolState = (ThreadPoolState) obj;

            threadPoolStateCache.putIfAbsent(threadPoolState.getThreadPoolName(), new ThreadPoolState());
            ThreadPoolState configThreadPoolState = threadPoolStateCache.get(threadPoolState.getThreadPoolName());
            configThreadPoolState.flushState(threadPoolState);

            log.debug("上报线程池信息 : {}", configThreadPoolState);
        }
    }

    /**
     * 客户端断开
     *
     * @param channel 客户端连接
     */
    public void clientClose(Channel channel) {
        String licenseKey = channel.attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).get();
        if (licenseKey == null || licenseKey.isEmpty()) return;

        ClientInfo clientInfo = licenseClientCache.get(licenseKey);
        if (clientInfo == null) return;

        // 删除客户端信息
        List<ClientInfo> clientInfoList = appKeyClientCache.get(OpentpServerConstant.ADMIN_DEFAULT_APP);
        clientInfoList.remove(clientInfo);

        // 删除线程记录
        clientThreadPoolStateCache.remove(clientInfo);

        // 关闭连接
        Channel cachedChannel = clientChannelCache.get(clientInfo);
        if (!cachedChannel.equals(channel)) {
            cachedChannel.close();
        }
        channel.close();
    }

    public void send(String channelKey, ThreadPoolState data) {

        AtomicReference<Channel> channelRef = new AtomicReference<>();
        clientChannelCache.forEach((key, value) -> {
            if (key.clientInfoKey().equals(channelKey)) {
                channelRef.set(value);
            }
        });
        if (channelRef.get() == null) {
            throw new IllegalArgumentException("客户端已断开, 更新线程信息失败!");
        }

        log.debug("线程池更新任务下发： {}", JacksonUtil.toJSONString(data));
        OpentpMessage opentpMessage = OpentpCoreConstant.OPENTP_MSG_PROTO.clone();
        OpentpMessage.builder()
                .messageType(OpentpMessageTypeEnum.THREAD_POOL_UPDATE.getCode())
                .serializerType(SerializerTypeEnum.Kryo.getType())
                .data(data)
                .traceId(MessageTraceIdUtil.traceId())
                .buildTo(opentpMessage);

        channelRef.get().writeAndFlush(opentpMessage);
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

    public Map<String, List<ClientInfo>> appKeyClientCache() {
        return appKeyClientCache;
    }

    public Map<String, ClientInfo> licenseClientCache() {
        return licenseClientCache;
    }

    public Map<ClientInfo, Channel> clientChannelCache() {
        return clientChannelCache;
    }

    public Map<ClientInfo, Map<String, ThreadPoolState>> clientThreadPoolStateCache() {
        return clientThreadPoolStateCache;
    }
}
