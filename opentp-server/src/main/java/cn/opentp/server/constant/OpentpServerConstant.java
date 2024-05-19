package cn.opentp.server.constant;

public class OpentpServerConstant {

    // todo 后续迭代认证服务
    private static final String DEFAULT_USER = "admin";
    private static final String DEFAULT_PW = "123456";
    // todo 后续迭代去掉
    public static final String ADMIN_DEFAULT_APP = "opentp";
    public static final String ADMIN_DEFAULT_SECRET = "opentp-secret";

    // 默认配置文件地址
    public static final String DEFAULT_CONFIG_FILE = "app.yml";

    // 默认上报服务端口
    public static final int DEFAULT_REPORT_SERVER_PORT = 9527;
    // 默认集群同步服务端口
    public static final int DEFAULT_TRANSPORT_SERVER_PORT = 9528;
    // 默认 REST API 服务端口
    public static final int DEFAULT_REST_SERVER_PORT = 8001;

    public static final String URI_SPLIT = "/";
}
