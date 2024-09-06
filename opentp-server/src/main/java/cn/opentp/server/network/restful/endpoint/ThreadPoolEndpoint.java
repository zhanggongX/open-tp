package cn.opentp.server.network.restful.endpoint;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.network.restful.annotation.GetMapping;
import cn.opentp.server.network.restful.annotation.PutMapping;
import cn.opentp.server.network.restful.annotation.RequestMapping;
import cn.opentp.server.network.restful.annotation.RestController;
import cn.opentp.server.network.restful.dto.BaseRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 线程池数据增删改查
 */
@RestController
@RequestMapping("/thread-pools")
public class ThreadPoolEndpoint {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    OpentpApp opentpApp = OpentpApp.instance();

    @GetMapping("/{appKey}")
    public BaseRes<Map<ClientInfo, Map<String, ThreadPoolState>>> threadPools(String appKey) {

        Map<ClientInfo, Map<String, ThreadPoolState>> clientInfoMap = opentpApp.receiveService().clientThreadPoolStateCache();
        return BaseRes.success(clientInfoMap.entrySet().stream()
                .filter(e -> e.getKey().getAppKey().equals(appKey))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @GetMapping("/{appKey}/{ip}")
    public BaseRes<Map<ClientInfo, Map<String, ThreadPoolState>>> infosByAppKeyAndIp(String appKey, String ip) {
        OpentpApp opentpApp = OpentpApp.instance();
        Map<ClientInfo, Map<String, ThreadPoolState>> clientInfoMap = opentpApp.receiveService().clientThreadPoolStateCache();
        return BaseRes.success(clientInfoMap.entrySet().stream()
                .filter(e -> e.getKey().getHost().equals(ip))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @GetMapping("/{appKey}/{ip}/{instance}")
    public BaseRes<Map<ClientInfo, Map<String, ThreadPoolState>>> infosByAppKeyAndIpAndInstance(String appKey, String ip, String instance) {
        OpentpApp opentpApp = OpentpApp.instance();
        Map<ClientInfo, Map<String, ThreadPoolState>> clientInfoMap = opentpApp.receiveService().clientThreadPoolStateCache();
        return BaseRes.success(clientInfoMap.entrySet().stream()
                .filter(e -> e.getKey().getInstance().equals(instance))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @GetMapping("/{appKey}/{ip}/{instance}/{name}")
    public BaseRes<Map<ClientInfo, Map<String, ThreadPoolState>>> infosByAppKeyAndIpAndInstanceAndTpName(String appKey, String ip, String instance, String name) {
        OpentpApp opentpApp = OpentpApp.instance();
        Map<ClientInfo, Map<String, ThreadPoolState>> clientInfoMap = opentpApp.receiveService().clientThreadPoolStateCache();
        return BaseRes.success(clientInfoMap.entrySet().stream().filter(e -> e.getKey().equals(name)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    /**
     * /tpInfos/{appKey}/{ip}/{instance}/{tpName}
     * curl -X PUT -H "Content-Type: application/json" -d '{"coreSize":10}' http://localhost:8080/tpInfos/opentp/192.168.100.200/83280/tp1
     */
    @PutMapping("/{appKey}/{ip}/{instance}/{name}")
    public BaseRes<Void> doPut(String appKey, String ip, String instance, String name, String request) {

        String clientInfoKey = appKey + "/" + ip + "/" + instance;

        Map<ClientInfo, Map<String, ThreadPoolState>> clientThreadPoolStateCache = OpentpApp.instance().receiveService().clientThreadPoolStateCache();
        AtomicReference<Map<String, ThreadPoolState>> threadPoolStateCacheRef = new AtomicReference<>();
        clientThreadPoolStateCache.forEach((key, value) -> {
            if (key.clientInfoKey().equals(clientInfoKey)) {
                threadPoolStateCacheRef.set(value);
            }
        });
        if (threadPoolStateCacheRef.get() == null) throw new IllegalArgumentException("路径错误");

        ThreadPoolState threadPoolState = threadPoolStateCacheRef.get().get(name);
        if (threadPoolState == null) throw new IllegalArgumentException("未知的线程池信息");

        return BaseRes.success();
    }
}
