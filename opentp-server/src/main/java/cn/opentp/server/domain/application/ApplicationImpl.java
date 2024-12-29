package cn.opentp.server.domain.application;

import java.util.List;

/**
 * 应用领域实现
 *
 * @author zg
 */
public class ApplicationImpl implements Application {

    /**
     * appName 推荐英文
     */
    private String appName;

    /**
     * name 显示名称，推荐中文
     */
    private String name;

    /**
     * app key - opentp 客户端配置使用
     */
    private String appKey;

    /**
     * app secret - opentp 客户端配置使用
     */
    private String appSecret;

    /**
     * 该应用的负责人
     */
    List<Manager> managers;

    /**
     * 处理注册命令
     *
     * @param command registration command
     */
    @Override
    public void handle(ApplicationRegCommand command) {

    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<Manager> getManagers() {
        return managers;
    }

    public void setManagers(List<Manager> managers) {
        this.managers = managers;
    }
}
