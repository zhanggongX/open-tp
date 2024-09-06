package cn.opentp.server.network.restful.dto.res;

public class AppCreateRes {

    private String appKey;
    private String appSecret;


    public AppCreateRes() {
    }

    public AppCreateRes(String appKey, String appSecret) {
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
