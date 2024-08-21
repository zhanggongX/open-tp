package cn.opentp.server.network.restful.dto;

public enum BaseResCode {

    SUCCESS("200", "成功"),
    FAIL("500", "失败"),
    NOT_FOUND("404", "未找到"),
    BAD_REQUEST("400", "请求错误"),
    UNAUTHORIZED("401", "未授权"),
    FORBIDDEN("403", "禁止访问"),
    NOT_ACCEPTABLE("406", "请求格式错误"),
    METHOD_NOT_ALLOWED("405", "请求方法错误"),
    INTERNAL_SERVER_ERROR("500", "服务器错误"),
    SERVICE_UNAVAILABLE("503", "服务不可用");

    private final String code;
    private final String message;

    BaseResCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
