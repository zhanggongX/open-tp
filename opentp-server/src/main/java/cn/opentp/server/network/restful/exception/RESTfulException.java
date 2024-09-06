package cn.opentp.server.network.restful.exception;

import cn.opentp.server.network.restful.dto.BaseResCode;

/**
 * RESTFul 业务异常
 *
 * @author zg
 */
public class RESTfulException extends RuntimeException {

    private final String code;
    private final String message;

    public RESTfulException(BaseResCode baseResCode, String message) {
        super(message);
        this.code = baseResCode.getCode();
        this.message = message;
    }

    public RESTfulException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public RESTfulException(BaseResCode baseResCode) {
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
