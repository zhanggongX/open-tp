package cn.opentp.server.rest;

import cn.opentp.server.exception.EndpointUnSupportException;
import cn.opentp.server.rest.endpoint.DefaultEndpoint;
import cn.opentp.server.rest.endpoint.Endpoint;
import cn.opentp.server.rest.endpoint.FaviconEndpoint;
import cn.opentp.server.rest.endpoint.OpentpEndpoint;

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
        endPoints.put("favicon.ico", new FaviconEndpoint());
        endPoints.put("opentp", new OpentpEndpoint());
        endPoints.put("", new DefaultEndpoint());
    }


    public Endpoint mappingHandler(String endPoint) {
        Endpoint endpoint = endPoints.get(endPoint);
        if (endpoint == null) {
            throw new EndpointUnSupportException();
        }
        return endpoint;
    }
}
