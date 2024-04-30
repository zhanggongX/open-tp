package cn.opentp.server.rest;

import cn.opentp.server.exception.EndpointUnSupportException;
import cn.opentp.server.rest.endpoint.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * endpoint 映射
 */
public class EndpointMapping {

    private final Map<String, Endpoint> endPoints = new ConcurrentHashMap<>();

    /**
     * todo 增加扫描的方式添加
     */
    public EndpointMapping() {
        // 添加 handler
        endPoints.put("", new DefaultEndpoint());
        endPoints.put("favicon.ico", new FaviconEndpoint());

        endPoints.put("appKeys", new AppKeysEndpoint());
        endPoints.put("tpInfos", new TpInfosEndpoint());
    }


    public Endpoint mappingHandler(String endPoint) {
        Endpoint endpoint = endPoints.get(endPoint);
        if (endpoint == null) {
            throw new EndpointUnSupportException();
        }
        return endpoint;
    }
}
