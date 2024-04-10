package cn.opentp.server.http.handler;

import cn.opentp.server.http.BaseRes;
import io.netty.handler.codec.http.FullHttpRequest;

public interface HttpHandler<T> {

    /**
     * 查询
     */
    BaseRes<T> doGet(FullHttpRequest request);

    /**
     * 新增
     *
     * @return
     */
    BaseRes<Void> doPost(FullHttpRequest request);

    /**
     * 修改
     *
     * @return
     */
    BaseRes<Void> doPut(FullHttpRequest request);

    /**
     * 删除
     *
     * @return
     */
    BaseRes<Void> doDelete(FullHttpRequest request);
}
