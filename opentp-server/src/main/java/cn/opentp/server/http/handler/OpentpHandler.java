package cn.opentp.server.http.handler;

import cn.opentp.core.tp.ThreadPoolWrapper;
import cn.opentp.server.http.BaseRes;
import cn.opentp.server.http.annotation.RequestURI;
import cn.opentp.server.tp.Configuration;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.Map;

//@RequestURI(url = "opentp")
public class OpentpHandler implements HttpHandler<Map<String, ThreadPoolWrapper>> {


    @Override
    public BaseRes<Map<String, ThreadPoolWrapper>> doGet(FullHttpRequest request) {
        String uri = request.uri();
        if (!uri.startsWith("/opentp")) {
            throw new IllegalArgumentException("错误的路径");
        }
        if (uri.equals("/opentp")) {
            return BaseRes.success(Configuration.configuration().getTpCache());
        }
        String[] split = uri.split("/");
        String tpName = null;
        if (split.length > 2) {
            tpName = split[2];
        }
        if (tpName == null || tpName.isEmpty()) {
            throw new IllegalArgumentException("错误的tpName");
        }

        Configuration configuration = Configuration.configuration();
        Map<String, Channel> tpChannel = configuration.getTpChannel();
        Channel channel = tpChannel.get(tpName);
        ThreadPoolWrapper threadPoolWrapper = new ThreadPoolWrapper();
        threadPoolWrapper.setDefault();
        threadPoolWrapper.setThreadName(tpName);
        threadPoolWrapper.setPoolSize(20);
        channel.writeAndFlush(threadPoolWrapper);

        return BaseRes.success();
    }

    @Override
    public BaseRes<Void> doPost(FullHttpRequest request) {
        return null;
    }

    @Override
    public BaseRes<Void> doPut(FullHttpRequest request) {
        return null;
    }

    @Override
    public BaseRes<Void> doDelete(FullHttpRequest request) {
        return null;
    }
}
