package cn.opentp.server.network.restful;

import cn.opentp.server.network.restful.netty.handler.RestServiceNettyHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RestfulService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // rest endpoint 映射
    private final EndpointMappings endpointMapping = new EndpointMappings();

    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final NioEventLoopGroup workGroup = new NioEventLoopGroup(1);

    public void start(String host, int port) {
        serverInit();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new HttpServerCodec());
                        socketChannel.pipeline().addLast(new HttpObjectAggregator(65536));
                        socketChannel.pipeline().addLast(new RestServiceNettyHandler());
                    }
                });

        ChannelFuture channelFuture = serverBootstrap.bind(host, port);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("RESTFul 服务启动，主机：{}，端口：{}", host, port);
                } else {
                    log.error("RESTFul 服务启动启动失败：", future.cause());
                }
            }
        });
    }

    public void serverInit() {
        EndpointMappingFactory endpointMappingFactory = new EndpointMappingFactory();
        try {
            endpointMappingFactory.registerMappings("cn.opentp.server.network.restful.endpoint");
        } catch (IOException e) {
            log.error("RESTFul 服务初始化失败：", e);
            throw new RuntimeException(e);
        }
    }

    public void handle(ChannelHandlerContext ctx, FullHttpRequest httpRequest) {

        boolean is100ContinueExpected = HttpUtil.is100ContinueExpected(httpRequest);
        if (is100ContinueExpected) {
            // 100 continue expected 处理
            ctx.writeAndFlush(new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.CONTINUE));
        }

        boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);
        // 生成 httpResponse
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.OK);
        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        if (keepAlive) {
            if (!httpRequest.protocolVersion().isKeepAliveDefault()) {
                httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
        } else {
            httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }

        // dispatcher 分发 http 请求
        EndpointDispatcher.dispatcher(httpRequest, httpResponse);

        // 返回 httpResponse 信息
        ChannelFuture channelFuture = ctx.writeAndFlush(httpResponse);
        if (!keepAlive) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    public void send(String channelKey, Object data) {
        throw new UnsupportedOperationException();
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

    public EndpointMappings endpointMapping() {
        return endpointMapping;
    }
}
