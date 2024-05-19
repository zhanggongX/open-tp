package cn.opentp.server.network;

import io.netty.channel.ChannelHandlerContext;

public interface NetworkService<H, S> {

    /**
     * 开启服务
     *
     * @param host ip
     * @param port 端口
     */
    void start(String host, int port);

    /**
     * 处理消息
     *
     * @param ctx  channelHandler环境
     * @param data 数据信息
     */
    void handle(ChannelHandlerContext ctx, H data);

    /**
     * 像客户端发送信息
     *
     * @param data
     */
    void send(String channelKey, S data);

    /**
     * 关闭服务
     */
    void close();
}
