package cn.opentp.server.network.restful;

import cn.opentp.server.exception.EndpointUnSupportException;
import cn.opentp.server.network.restful.endpoint.*;

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
        // 默认处理
        endPoints.put("", new DefaultEndpoint());
        // favicon
        endPoints.put("favicon.ico", new FaviconEndpoint());

        // 自定义端点处理
        endPoints.put("appKeys", new AppKeysEndpoint());
        endPoints.put("tpInfo", new TpInfoEndpoint());
    }


    public Endpoint mappingHandler(String endPoint) {
        Endpoint endpoint = endPoints.get(endPoint);
        if (endpoint == null) {
            throw new EndpointUnSupportException();
        }
        return endpoint;
    }
}
