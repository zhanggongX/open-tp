package cn.opentp.server.domain.application;

import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.DomainException;
import cn.opentp.server.domain.EventQueue;
import cn.opentp.server.domain.connect.ConnectCommand;
import cn.opentp.server.domain.manager.ManagerImpl;

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
    private String showName;

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
    List<String> managers;

    /**
     * 处理注册命令
     *
     * @param eventQueue 事件队列
     * @param command    registration command
     */
    @Override
    public void handle(EventQueue eventQueue, ApplicationCreateCommand command) {
        String username = OpentpApp.instance().getUsername();
        eventQueue.offer(new ApplicationCreateEvent(command.getAppName(), username));
    }

    @Override
    public void checkConnect(String appKey, String appSecret) {
        if (!this.getAppKey().equals(appKey) || !this.getAppSecret().equals(appSecret)) {
            throw new DomainException("auth fail");
        }
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
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

    public List<String> getManagers() {
        return managers;
    }

    public void setManagers(List<String> managers) {
        this.managers = managers;
    }
}
