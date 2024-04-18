package cn.opentp.server.http.handler;

import cn.opentp.core.tp.ThreadPoolContext;
import cn.opentp.server.http.BaseRes;
import cn.opentp.server.tp.Configuration;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.lang.reflect.Field;
import java.util.Map;

//@RequestURI(url = "opentp")
public class OpentpHandler extends AbstractHttpHandler implements HttpHandler {

    @Override
    public void doGet(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {

        String uri = httpRequest.uri();
        if (!uri.startsWith("/opentp")) {
            throw new IllegalArgumentException("错误的路径");
        }

        Map<String, ThreadPoolContext> tpCache = Configuration.configuration().getTpCache();

        String tpName = null;
        String[] urlPaths = uri.split("/");
        if (urlPaths.length == 2) {
            BaseRes<Map<String, ThreadPoolContext>> res = BaseRes.success(tpCache);
            updateHttpResponse(httpResponse, res);
            return;
        }

        if (urlPaths.length > 2) {
            tpName = urlPaths[2];
        }
        if (tpName == null || tpName.isEmpty()) {
            BaseRes<Void> res = BaseRes.fail(-1, "错误的tpName");
            updateHttpResponse(httpResponse, res);
            return;
        }

        ThreadPoolContext threadPoolWrapper = tpCache.get(tpName);
        updateHttpResponse(httpResponse, threadPoolWrapper);
    }


    @Override
    public void doPost(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {

    }

    @Override
    public void doPut(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {

        String uri = httpRequest.uri();
        if (!uri.startsWith("/opentp")) {
            throw new IllegalArgumentException("错误的路径");
        }
        String[] urlPaths = uri.split("/");
        if (urlPaths.length != 5) {
            BaseRes<Void> res = BaseRes.fail(-1, "错误的更新方法");
            updateHttpResponse(httpResponse, res);
            return;
        }
        String tpName = urlPaths[2];
        String param = urlPaths[3];
        int value = Integer.parseInt(urlPaths[4]);

        Field declaredField = null;
        try {
            declaredField = ThreadPoolContext.class.getDeclaredField(param);
            declaredField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        if(declaredField == null){
            BaseRes<Void> res = BaseRes.fail(-1, "无效的参数");
            updateHttpResponse(httpResponse, res);
            return;
        }

        Configuration configuration = Configuration.configuration();
        Map<String, Channel> tpChannel = configuration.getTpChannel();
        Channel channel = tpChannel.get(tpName);
        ThreadPoolContext threadPoolWrapper = new ThreadPoolContext();
        threadPoolWrapper.setDefault();
        threadPoolWrapper.setThreadName(tpName);

        try {
            declaredField.set(threadPoolWrapper, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        channel.writeAndFlush(threadPoolWrapper);

        BaseRes<Void> res = BaseRes.success();
        updateHttpResponse(httpResponse, res);
    }

    @Override
    public void doDelete(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {

    }
}
