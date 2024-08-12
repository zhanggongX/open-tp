package cn.opentp.client.network;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.client.exception.ServerAddrUnDefineException;
import cn.opentp.client.network.netty.handler.ReportServiceNettyHandler;
import cn.opentp.core.auth.License;
import cn.opentp.core.constant.OpentpCoreConstant;
import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageConstant;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.core.net.codec.OpentpMessageDecoder;
import cn.opentp.core.net.codec.OpentpMessageEncoder;
import cn.opentp.core.net.serializer.SerializerTypeEnum;
import cn.opentp.core.thread.pool.ThreadPoolWrapper;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.core.util.JacksonUtil;
import cn.opentp.core.util.MessageTraceIdUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ThreadPoolReportService {

    private final static Logger log = LoggerFactory.getLogger(ThreadPoolReportService.class);

    private final Bootstrap clientBootstrap = new Bootstrap();
    private final NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(1);
    // 线程池信息上报 socket
    private Channel channel;

    public void startup() {
        this.config();
        this.connect();
    }

    public void config() {
        clientBootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // 每五秒一直没有发送任何消息，则发生一个心跳
                        socketChannel.pipeline().addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        socketChannel.pipeline().addLast(new OpentpMessageEncoder());
                        socketChannel.pipeline().addLast(new OpentpMessageDecoder());
                        socketChannel.pipeline().addLast(new ReportServiceNettyHandler());
                    }
                });
    }

    public void connect() {
        // 配置的服务器信息
        List<InetSocketAddress> inetSocketAddresses = Configuration._cfg().serverAddresses();
        if (inetSocketAddresses.isEmpty()) {
            throw new ServerAddrUnDefineException();
        }

        // todo 集群迭代。
        log.debug("服务端地址：{}, 端口：{}", inetSocketAddresses.get(0).getAddress().getHostAddress(), inetSocketAddresses.get(0).getPort());
        log.debug("尝试连接服务器...");
        ChannelFuture channelFuture = clientBootstrap.connect(inetSocketAddresses.get(0));

        // 监听连接状态
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    // 发送权限信息
                    sendAuth(channelFuture.channel());
                }else {
                    log.error("连接 opentp 集群失败，请检查 IP 和端口是否配置正确。");
                }
            }
        });
    }

    public void sendAuth(Channel channel) {
        Configuration configuration = Configuration._cfg();

        channel.attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).set(Strings.EMPTY);
        this.channel = channel;

        // 发送权限验证
        OpentpMessage opentpMessage = OpentpCoreConstant.OPENTP_MSG_PROTO.clone();
        OpentpMessage
                .builder()
                .messageType(OpentpMessageTypeEnum.AUTHENTICATION_REQ.getCode())
                .serializerType(SerializerTypeEnum.Kryo.getType())
                .traceId(MessageTraceIdUtil.traceId())
                .data(configuration.clientInfo())
                .buildTo(opentpMessage);

        this.channel.writeAndFlush(opentpMessage);
    }

    public void sendReport() {
        // 连接为空，连接断开，连接 licenseKey 为空，都不发送消息
        if (!check()) {
            return;
        }

        List<ThreadPoolState> threadPoolStates = new ArrayList<>();

        Map<String, ThreadPoolWrapper> threadPoolContextCache = Configuration._cfg().threadPoolContextCache();
        for (Map.Entry<String, ThreadPoolWrapper> threadPoolContextEntry : threadPoolContextCache.entrySet()) {
            String threadPoolKey = threadPoolContextEntry.getKey();
            ThreadPoolWrapper threadPoolContext = threadPoolContextEntry.getValue();
            threadPoolContext.flushStateAndSetThreadPoolName(threadPoolKey);
            threadPoolStates.add(threadPoolContext.getState());
        }

        OpentpMessage opentpMessage = OpentpCoreConstant.OPENTP_MSG_PROTO.clone();
        OpentpMessage
                .builder()
                .messageType(OpentpMessageTypeEnum.THREAD_POOL_EXPORT.getCode())
                .serializerType(SerializerTypeEnum.Kryo.getType())
                .traceId(MessageTraceIdUtil.traceId())
                .licenseKey(this.channel.attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).get())
                .data(threadPoolStates)
                .buildTo(opentpMessage);

        ChannelFuture channelFuture = this.channel.writeAndFlush(opentpMessage);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    log.debug("上报线程消息成功");
                } else {
                    log.error("上报信息异常 : ", channelFuture.cause());
                }
            }
        });
    }

    public void heartbeat() {
        OpentpMessage opentpMessage = OpentpCoreConstant.OPENTP_MSG_PROTO.clone();
        OpentpMessage.builder()
                .messageType(OpentpMessageTypeEnum.HEART_PING.getCode())
                .serializerType(SerializerTypeEnum.Kryo.getType())
                .data(OpentpMessageConstant.HEARD_PING)
                .traceId(MessageTraceIdUtil.traceId())
                .buildTo(opentpMessage);

        log.info("发送心跳信息");
        this.channel.writeAndFlush(opentpMessage);
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
            case THREAD_POOL_UPDATE:
                ThreadPoolState threadPoolState = (ThreadPoolState) opentpMessage.getData();
                log.debug("接收到线程池更新命令：{}", JacksonUtil.toJSONString(threadPoolState));

                Map<String, ThreadPoolWrapper> threadPoolContextCache = Configuration._cfg().threadPoolContextCache();
                ThreadPoolWrapper threadPoolContext = threadPoolContextCache.get(threadPoolState.getThreadPoolName());

                threadPoolContext.flushTargetState(threadPoolState);
                break;
            case AUTHENTICATION_RES:
                License opentpLicense = (License) opentpMessage.getData();
                Configuration configuration = Configuration._cfg();
                // 认证成功，设置 license
                this.channel.attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).set(opentpLicense.getLicenseKey());
                break;
            default:
                log.warn("未知的消息类型，不处理！");
        }
    }

    public void connectCheck() {
        if (check()) {
            return;
        }
        // todo 不同的重连策略
        this.connect();
    }

    private boolean check() {
        return this.channel != null && this.channel.isActive() && !this.channel.attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).get().isEmpty();
    }

    public void close() {
        nioEventLoopGroup.shutdownGracefully();
    }
}
