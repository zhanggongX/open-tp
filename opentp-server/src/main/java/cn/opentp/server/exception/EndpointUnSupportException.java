package cn.opentp.server.exception;

public class EndpointUnSupportException extends RuntimeException {

    private final static String DEFAULT_ERROR = "不支持的 endpoint 操作";

    public EndpointUnSupportException() {
        super(DEFAULT_ERROR);
    }
}
