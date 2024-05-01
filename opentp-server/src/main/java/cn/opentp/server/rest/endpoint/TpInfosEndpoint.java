package cn.opentp.server.rest.endpoint;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.constant.OpentpCoreConstant;
import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageConstant;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.core.net.serializer.SerializerTypeEnum;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.core.util.JSONUtils;
import cn.opentp.core.util.MessageTraceIdUtil;
import cn.opentp.server.configuration.Configuration;
import cn.opentp.server.exception.EndpointUnSupportException;
import cn.opentp.server.rest.BaseRes;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

        Configuration configuration = Configuration.configuration();
        Map<String, Map<String, ThreadPoolState>> clientKeyThreadPoolStatesCache = configuration.clientKeyThreadPoolStatesCache();
        ThreadPoolState threadPoolState = clientKeyThreadPoolStatesCache.get(clientInfoKey).get(tpName);

        if (threadPoolState == null) throw new IllegalArgumentException("未知的线程池信息");

        String content = httpRequest.content().toString(CharsetUtil.UTF_8);
        JsonNode jsonNode = JSONUtils.getNode(content);

        ThreadPoolState newThreadPoolState = new ThreadPoolState();
        newThreadPoolState.flushDefault(threadPoolState.getThreadPoolName());
        newThreadPoolState.flushRequest(jsonNode);

        Channel channel = configuration.clientKeyChannelCache().get(clientInfoKey);
        log.debug("线程池更新任务下发： {}", JSONUtils.toJson(newThreadPoolState));
        OpentpMessage opentpMessage = OpentpCoreConstant.OPENTP_MSG_PROTO.clone();
        OpentpMessage
                .builder()
                .messageType(OpentpMessageTypeEnum.THREAD_POOL_UPDATE.getCode())
                .serializerType(SerializerTypeEnum.Kryo.getType())
                .data(newThreadPoolState)
                .traceId(MessageTraceIdUtil.traceId())
                .buildTo(opentpMessage);

        channel.writeAndFlush(opentpMessage);

        return BaseRes.success();
    }

    @Override
    public BaseRes<Void> doDelete(FullHttpRequest httpRequest, FullHttpResponse httpResponse) {
        throw new EndpointUnSupportException();
    }
}
