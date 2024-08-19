package cn.opentp.server.network.restful.mapping;

/**
 * 请求映射参数类型枚举
 *
 * @author zg
 */
public enum EndPointMappingParamType {

    /**
     * Request Url
     */
    REQUEST_PARAM,

    /**
     * 路径变量
     */
    PATH_VARIABLE,

    /**
     * Http Request
     */
    HTTP_REQUEST,

    /**
     * Http Response
     */
    HTTP_RESPONSE,

    /**
     * 请求体
     */
    REQUEST_BODY,

    /**
     * X-WWW-FORM-URLENCODED
     */
    URL_ENCODED_FORM,

    /**
     * Http request Header
     */
    REQUEST_HEADER,

    UPLOAD_FILE,

    UPLOAD_FILES
}
