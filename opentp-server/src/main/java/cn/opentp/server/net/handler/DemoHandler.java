package cn.opentp.server.net.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class DemoHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(DemoHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        try {
            byte[] barray = new byte[buf.readableBytes()];
            buf.getBytes(0, barray);
            String str = new String(barray);

            if (str.length() > 0) {
                System.out.println(str);
                System.out.println("收到消息回复一条消息给客户端");
                System.out.println("client channelActive..");
                ctx.writeAndFlush(Unpooled.copiedBuffer("服务器端发一条数据给客户端" + new Date().toString(), CharsetUtil.UTF_8));
                System.out.flush();

            } else {
                System.out.println("不能读啊");
            }
        } finally {
            buf.release();
        }
    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("server channelReadComplete..");
//
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("server occur exception:" + cause.getMessage());
        cause.printStackTrace();
        ctx.close(); // 关闭发生异常的连接
    }
}
