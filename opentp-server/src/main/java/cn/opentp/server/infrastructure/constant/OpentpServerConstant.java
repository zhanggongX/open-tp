package cn.opentp.server.infrastructure.constant;

public class OpentpServerConstant {

    // todo 后续迭代，认证授权后续完善。
    // 认证服务，默认登录人员
    public static final String DEFAULT_USER = "admin";
    // 默认登录人员密码
    public static final String DEFAULT_PW = "123456";

    // 默认配置文件地址
    public static final String DEFAULT_CONFIG_FILE = "opentp.yml";

    /**
     * 默认集群名
     */
    public static final String DEFAULT_CLUSTER = "opentp";
    /**
     * 默认数据目录
     */
    public static final String DEFAULT_PATH_DATA = "/data";
    /**
     * 默认日志目录
     */
    public static final String DEFAULT_PATH_LOGS = "/logs";
    /**
     * 默认上报服务端口
     */
    public static final int DEFAULT_REPORT_SERVER_PORT = 9527;
    /**
     * 默认集群同步服务端口
     */
    public static final int DEFAULT_TRANSPORT_SERVER_PORT = 9528;
    /**
     * 默认 REST API 服务端口
     */
    public static final int DEFAULT_REST_SERVER_PORT = 8001;

    public static final String URI_SPLIT = "/";
}
