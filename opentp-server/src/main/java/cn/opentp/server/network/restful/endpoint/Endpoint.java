package cn.opentp.server.network.restful.endpoint;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface Endpoint {

    /**
     * 查询
     */
    void get(FullHttpRequest httpRequest, FullHttpResponse httpResponse);

    /**
     * 新增
     */
    void post(FullHttpRequest httpRequest, FullHttpResponse httpResponse);

    /**
     * 修改
     */
    void put(FullHttpRequest httpRequest, FullHttpResponse httpResponse);

    /**
     * 删除
     */
    void delete(FullHttpRequest httpRequest, FullHttpResponse httpResponse);
}
