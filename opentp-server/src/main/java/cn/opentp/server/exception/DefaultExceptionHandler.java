package cn.opentp.server.exception;

import cn.opentp.server.network.restful.http.RestHttpRequest;
import cn.opentp.server.network.restful.http.RestHttpResponse;
import cn.opentp.server.network.restful.http.HttpStatus;
import io.netty.handler.codec.http.FullHttpRequest;

public class DefaultExceptionHandler implements ExceptionHandler{

    /**
     * 处理异常
     * @param e
     */
    @Override
    public void doHandle(Exception e, RestHttpRequest request, RestHttpResponse response) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if(e instanceof ResourceNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        }
        String errorMessage = e.getCause() == null ? "" : e.getCause().getMessage();
        if(errorMessage == null) {
            errorMessage = e.getMessage();
        }
//        HttpResponseWrap response = HttpContextHolder.getResponse();
        response.writeAndFlush(status, errorMessage);
        response.closeChannel();
    }
}
