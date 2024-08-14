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
import cn.opentp.server.network.receive.message.handler.AuthMessageHandler;
import cn.opentp.server.network.receive.message.handler.MessageHandler;
import cn.opentp.server.network.receive.message.handler.ReceiveMessageHandler;
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

    /**
     * 初始化
     */
    public ThreadPoolReceiveService() {
        // 消息处理器初始化
        MessageHandler.HANDLER_MAP.put(OpentpMessageTypeEnum.AUTHENTICATION_REQ, new AuthMessageHandler());
        MessageHandler.HANDLER_MAP.put(OpentpMessageTypeEnum.THREAD_POOL_EXPORT, new ReceiveMessageHandler());
    }

    /**
     * 服务启动
     *
     * @param host 主机地址
     * @param port 主机端口
     */
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
                    log.info("线程池信息接收服务监听成功，主机：{}，端口：{}", host, port);
                } else {
                    log.error("线程池信息接收服务监听失败：", future.cause());
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
        if (opentpMessageTypeEnum == null) {
            log.warn("未知的消息类型，不处理！");
            return;
        }

        if (opentpMessageTypeEnum == OpentpMessageTypeEnum.HEART_PING) {
            log.info("接收心跳信息： {} 应答: {}", opentpMessage.getData(), OpentpMessageConstant.HEARD_PONG);
            return;
        }

        MessageHandler messageHandler = MessageHandler.HANDLER_MAP.get(opentpMessageTypeEnum);
        if (messageHandler == null) {
            log.warn("未知的消息类型，不处理！");
            return;
        }

        messageHandler.handle(this, ctx, opentpMessage);
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
