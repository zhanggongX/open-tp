package cn.opentp.server.exception;

import cn.opentp.server.network.restful.http.RestHttpRequest;
import cn.opentp.server.network.restful.http.RestHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 异常处理器
 *
 * @author zg
 * @date 2018/3/16
 */
public interface ExceptionHandler {

    /**
     * 处理异常
     *
     * @param e
     */
    void doHandle(Exception e, RestHttpRequest request, RestHttpResponse response);
}