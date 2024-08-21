package cn.opentp.server.network.restful.exception;

import cn.opentp.server.network.restful.dto.BaseResCode;

/**
 * RESTFul 业务异常
 *
 * @author zg
 */
public class RestfulException extends RuntimeException {

    private final String code;
    private final String message;

    public RestfulException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public RestfulException(BaseResCode baseResCode) {
        super(baseResCode.getMessage());
        this.code = baseResCode.getCode();
        this.message = baseResCode.getMessage();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
