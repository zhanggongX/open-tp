package cn.opentp.server.rest.endpoint;

import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.server.rest.BaseRes;
import cn.opentp.server.configuration.Configuration;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 线程池数据增删改查
 */
public class OpentpEndpoint extends AbstractEndpointAdapter<Map<String, ThreadPoolState>> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public BaseRes<Map<String, ThreadPoolState>> doGet(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {

        Map<String, ThreadPoolState> map = new HashMap<>();

        String uri = httpRequest.uri();
        if (!uri.startsWith("/opentp")) {
            throw new IllegalArgumentException("错误的路径");
        }

        Map<String, ThreadPoolState> threadPoolStateCache = Configuration.configuration().threadPoolStateCache();

        String tpName = null;
        String[] urlPaths = uri.split("/");
        if (urlPaths.length == 2) {
            return BaseRes.success(threadPoolStateCache);
        }

        if (urlPaths.length > 2) {
            tpName = urlPaths[2];
        }
        if (tpName == null || tpName.isEmpty()) {
//            return BaseRes.fail(-1, "错误的tpName");
        }

        ThreadPoolState threadPoolState = threadPoolStateCache.get(tpName);
        map.put(tpName, threadPoolState);

        return BaseRes.success(map);
    }


    @Override
    public BaseRes<Void> doPost(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        return BaseRes.success();
    }

    @Override
    public BaseRes<Void> doPut(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {

        String uri = httpRequest.uri();
        if (!uri.startsWith("/opentp")) {
            throw new IllegalArgumentException("错误的路径");
        }
        String[] urlPaths = uri.split("/");
        if (urlPaths.length != 5) {
            return BaseRes.fail(-1, "错误的更新方法");
        }
        String theadPoolName = urlPaths[2];
        String param = urlPaths[3];
        int value = Integer.parseInt(urlPaths[4]);

        Field declaredField = null;
        try {
            declaredField = ThreadPoolState.class.getDeclaredField(param);
            declaredField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            log.error("", e);
        }

        if (declaredField == null) {
            return BaseRes.fail(-1, "无效的参数");
        }

        Configuration configuration = Configuration.configuration();
        Map<String, Channel> channelCache = configuration.channelCache();
        Channel channel = channelCache.get(theadPoolName);
        ThreadPoolState threadPoolState = new ThreadPoolState();
        threadPoolState.flushDefault(theadPoolName);

        try {
            declaredField.set(threadPoolState, value);
        } catch (IllegalAccessException e) {
            log.error("", e);
        }

        channel.writeAndFlush(threadPoolState);

        return BaseRes.success();
    }

    @Override
    public BaseRes<Void> doDelete(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        return BaseRes.success();
    }
}
