package cn.opentp.server.rest.controller;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface HttpHandler {

    /**
     * 查询
     */
    void doGet(FullHttpRequest httpRequest, FullHttpResponse httpResponse);

    /**
     * 新增
     *
     * @return
     */
    void doPost(FullHttpRequest httpRequest, FullHttpResponse httpResponse);

    /**
     * 修改
     *
     * @return
     */
    void doPut(FullHttpRequest httpRequest, FullHttpResponse httpResponse);

    /**
     * 删除
     *
     * @return
     */
    void doDelete(FullHttpRequest httpRequest, FullHttpResponse httpResponse);
}
