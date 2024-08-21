package cn.opentp.server.network.restful;

import cn.opentp.server.network.restful.http.RestHttpRequest;
import cn.opentp.server.network.restful.http.RestHttpResponse;
import cn.opentp.server.network.restful.mapping.EndpointMappingResolver;
import cn.opentp.server.network.restful.netty.handler.RestfulServiceNettyHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * RESTFul 服务
 *
 * @author zg
 */
public class RestfulService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RestfulDispatcher restfulDispatcher = new RestfulDispatcher();

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
                        socketChannel.pipeline().addLast(new RestfulServiceNettyHandler());
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
        EndpointMappingResolver endpointMappingResolver = new EndpointMappingResolver();
        try {
            endpointMappingResolver.registerMappings("cn.opentp.server.network.restful.endpoint");
        } catch (IOException e) {
            log.error("RESTFul 服务初始化失败：", e);
            throw new RuntimeException(e);
        }
    }

    public void handle(ChannelHandlerContext ctx, FullHttpRequest httpRequest) {
        RestHttpRequest request = new RestHttpRequest(httpRequest);
        RestHttpResponse response = new RestHttpResponse(ctx, new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer("", CharsetUtil.UTF_8)));
        restfulDispatcher.doDispatch(request, response);
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
