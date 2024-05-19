package cn.opentp.server.network.rest.endpoint;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.core.util.JacksonUtil;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.exception.EndpointUnSupportException;
import cn.opentp.server.network.rest.BaseRes;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 线程池数据增删改查
 */
public class TpInfoEndpoint extends AbstractEndpointAdapter<Map<ClientInfo, Map<String, ThreadPoolState>>> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final static String PRE_URI = "/tpInfo";

    @Override
    public BaseRes<Map<ClientInfo, Map<String, ThreadPoolState>>> doGet(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {

        String uri = httpRequest.uri();

        OpentpApp opentpApp = OpentpApp.instance();
        Map<ClientInfo, Map<String, ThreadPoolState>> clientThreadPoolStateCache = opentpApp.reportService().clientThreadPoolStateCache();

        String[] uris = uri.split("/");
        // /tpInfos/ || /tpInfos
        if (uris.length <= 2) {
            // todo 获取当前登录的 appKeys, 然后去获取该 appKeys 的线程池信息。
            return BaseRes.success(clientThreadPoolStateCache);
        }

        // /tpInfos/{appKey}
        String appKey = uris[2];
        // todo 目前只有一个 appKey 无需过滤。

        // /tpInfos/{appKey}/{ip}
        if (uris.length > 3) {
            String ip = uris[3];
            clientThreadPoolStateCache = clientThreadPoolStateCache.entrySet().stream().filter(e -> e.getKey().getHost().equals(ip)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        // /tpInfos/{appKey}/{ip}/{instance}
        if (uris.length > 4) {
            String instance = uris[4];
            clientThreadPoolStateCache = clientThreadPoolStateCache.entrySet().stream().filter(e -> e.getKey().getInstance().equals(instance)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        // /tpInfos/{appKey}/{ip}/{instance}/{tpName}
        if (uris.length > 5) {
            String tpName = uris[5];
            for (Map.Entry<ClientInfo, Map<String, ThreadPoolState>> entry : clientThreadPoolStateCache.entrySet()) {
                Map<String, ThreadPoolState> threadPoolStateMap = entry.getValue();
                threadPoolStateMap = threadPoolStateMap.entrySet().stream().filter(e -> e.getKey().equals(tpName)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                entry.setValue(threadPoolStateMap);
            }
        }

        return BaseRes.success(clientThreadPoolStateCache);
    }


    @Override
    public BaseRes<Void> doPost(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        throw new EndpointUnSupportException();
    }

    /**
     * /tpInfos/{appKey}/{ip}/{instance}/{tpName}
     * curl -X PUT -H "Content-Type: application/json" -d '{"coreSize":10}' http://localhost:8080/tpInfos/opentp/192.168.100.200/83280/tp1
     */
    @Override
    public BaseRes<Void> doPut(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {

        String uri = httpRequest.uri();
        String[] uris = uri.split("/");
        if (uris.length != 6) {
            throw new IllegalArgumentException("路径错误");
        }

        String appKey = uris[2];
        String ip = uris[3];
        String instance = uris[4];
        String tpName = uris[5];
        String clientInfoKey = appKey + "/" + ip + "/" + instance;

        Map<ClientInfo, Map<String, ThreadPoolState>> clientThreadPoolStateCache = OpentpApp.instance().reportService().clientThreadPoolStateCache();
        AtomicReference<Map<String, ThreadPoolState>> threadPoolStateCacheRef = new AtomicReference<>();
        clientThreadPoolStateCache.forEach((key, value) -> {
            if (key.clientInfoKey().equals(clientInfoKey)) {
                threadPoolStateCacheRef.set(value);
            }
        });
        if (threadPoolStateCacheRef.get() == null) throw new IllegalArgumentException("路径错误");

        ThreadPoolState threadPoolState = threadPoolStateCacheRef.get().get(tpName);
        if (threadPoolState == null) throw new IllegalArgumentException("未知的线程池信息");

        String content = httpRequest.content().toString(CharsetUtil.UTF_8);
        JsonNode jsonNode = JacksonUtil.getNode(content);

        ThreadPoolState newThreadPoolState = new ThreadPoolState();
        newThreadPoolState.flushDefault(threadPoolState.getThreadPoolName());
        newThreadPoolState.flushRequest(jsonNode);

        // 线程值更新
        OpentpApp.instance().reportService().send(clientInfoKey, newThreadPoolState);

        return BaseRes.success();
    }

    @Override
    public BaseRes<Void> doDelete(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        throw new EndpointUnSupportException();
    }
}
