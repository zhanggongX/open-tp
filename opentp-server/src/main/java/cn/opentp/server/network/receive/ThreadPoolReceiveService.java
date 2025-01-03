package cn.opentp.server.network.receive;

import cn.opentp.core.net.codec.OpentpMessageDecoder;
import cn.opentp.core.net.codec.OpentpMessageEncoder;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.server.domain.connect.ConnectImpl;
import cn.opentp.server.network.receive.handler.ReceiveServiceNettyHandler;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadPoolReceiveService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // key = appKey, value = 所有连接上来的客户端
    private final Map<String, List<ConnectImpl>> appKeyConnectCache = new ConcurrentHashMap<>();
    // key = 连接, value = 连接对应的 channel
    private final BiMap<ConnectImpl, Channel> connectChannelCache = HashBiMap.create();
    // row = 连接, col = 线程池名, value = 线程池信息
    private final Table<ConnectImpl, String, ThreadPoolState> connectThreadPoolStateTable = HashBasedTable.create();

    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
    private final NioEventLoopGroup workGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);

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
     * 客户端断开
     *
     * @param channel 客户端连接
     */
    public void clientClose(Channel channel) {

        // 获取当前 channel 对应的连接
        ConnectImpl connect = connectChannelCache.inverse().get(channel);

        log.info("connect, {} 断开连接", connect);

        // 删除连接
        List<ConnectImpl> connects = appKeyConnectCache.get(connect.getAppKey());
        connects.remove(connect);
        appKeyConnectCache.put(connect.getAppKey(), connects);

        // 删除连接记录
        connectChannelCache.remove(connect);

        // 删除线程记录
        connectThreadPoolStateTable.rowKeySet().remove(connect);

        // 关闭线程
        channel.close();
    }

//    public void send(String channelKey, ThreadPoolState data) {
//
//        AtomicReference<Channel> channelRef = new AtomicReference<>();
//        clientChannelCache.forEach((key, value) -> {
//            if (key.clientInfoKey().equals(channelKey)) {
//                channelRef.set(value);
//            }
//        });
//        if (channelRef.get() == null) {
//            throw new IllegalArgumentException("客户端已断开, 更新线程信息失败!");
//        }
//
//        log.debug("线程池更新任务下发： {}", JacksonUtil.toJSONString(data));
//        OpentpMessage opentpMessage = OpentpCoreConstant.OPENTP_MSG_PROTO.clone();
//        OpentpMessage.builder()
//                .messageType(OpentpMessageTypeEnum.THREAD_POOL_UPDATE.getCode())
//                .serializerType(SerializerTypeEnum.Kryo.getType())
//                .data(data)
//                .traceId(MessageTraceIdUtil.traceId())
//                .buildTo(opentpMessage);
//
//        channelRef.get().writeAndFlush(opentpMessage);
//    }

    public void close() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

    public Map<String, List<ConnectImpl>> appKeyConnectCache() {
        return appKeyConnectCache;
    }

    public BiMap<ConnectImpl, Channel> connectChannelCache() {
        return connectChannelCache;
    }

    public Table<ConnectImpl, String, ThreadPoolState> connectThreadPoolStateTable() {
        return connectThreadPoolStateTable;
    }
}
