package cn.opentp.server.rest.endpoint;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.server.configuration.Configuration;
import cn.opentp.server.rest.BaseRes;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 线程池数据增删改查
 */
public class TpInfosEndpoint extends AbstractEndpointAdapter<Map<ClientInfo, Map<String, ThreadPoolState>>> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final static String PRE_URI = "/tpInfos";

    @Override
    public BaseRes<Map<ClientInfo, Map<String, ThreadPoolState>>> doGet(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {

        String uri = httpRequest.uri();

        Configuration configuration = Configuration.configuration();
        Map<ClientInfo, Map<String, ThreadPoolState>> clientThreadPoolStatesCache = configuration.clientThreadPoolStatesCache();

        String[] uris = uri.split("/");
        // /tpInfos/ || /tpInfos
        if (uris.length <= 2) {
            // todo 获取当前登录的 appKeys, 然后去获取该 appKeys 的线程池信息。
            return BaseRes.success(clientThreadPoolStatesCache);
        }

        // /tpInfos/{appKey}
        String appKey = uris[2];
        // todo 目前只有一个 appKey 无需过滤。

        // /tpInfos/{appKey}/{ip}
        if (uris.length > 3) {
            String ip = uris[3];
            clientThreadPoolStatesCache = clientThreadPoolStatesCache
                    .entrySet().stream()
                    .filter(e -> e.getKey().getHost().equals(ip))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue));
        }

        // /tpInfos/{appKey}/{ip}/{instance}
        if (uris.length > 4) {
            String instance = uris[4];
            clientThreadPoolStatesCache = clientThreadPoolStatesCache
                    .entrySet().stream()
                    .filter(e -> e.getKey().getInstance().equals(instance))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue));
        }

        // /tpInfos/{appKey}/{ip}/{instance}/{tpName}
        if (uris.length > 5) {
            String tpName = uris[5];
            for (Map.Entry<ClientInfo, Map<String, ThreadPoolState>> entry : clientThreadPoolStatesCache.entrySet()) {
                Map<String, ThreadPoolState> threadPoolStateMap = entry.getValue();
                threadPoolStateMap = threadPoolStateMap.entrySet().stream()
                        .filter(e -> e.getKey().equals(tpName))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                entry.setValue(threadPoolStateMap);
            }
        }

        return BaseRes.success(clientThreadPoolStatesCache);
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
        Map<String, Channel> channelCache = null; //configuration.channelCache();
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
