package cn.opentp.server.auth;

import java.util.UUID;

public class LicenseKeyFactory {

    /**
     * todo 多种 key 生成策略
     *
     * @return LicenseKey
     */
    public static String get() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
