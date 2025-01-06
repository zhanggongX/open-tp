package cn.opentp.server.domain.application;

import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.DomainException;
import cn.opentp.server.domain.EventQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 应用领域实现
 *
 * @author zg
 */
public class ApplicationImpl implements Application {

    private final Logger log = LoggerFactory.getLogger(ApplicationImpl.class);

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
        String username = OpentpApp.instance().getManagerUsername();
        eventQueue.offer(new ApplicationCreateEvent(this.appKey, username));
    }

    @Override
    public void checkConnect(String appKey, String appSecret) {
        if (!this.getAppKey().equals(appKey) || !this.getAppSecret().equals(appSecret)) {
            throw new DomainException("auth fail");
        }
    }

    @Override
    public void handle(EventQueue eventQueue, ApplicationDeleteCommand command) {
        log.info("delete application: {}, appKey: {}", this.getAppName(), command.getAppKey());
        eventQueue.offer(new ApplicationDeleteEvent(command.getAppKey(), this.managers));
    }

    @Override
    public void handle(EventQueue eventQueue, ApplicationUpdateCommand applicationUpdateCommand) {
        this.showName = applicationUpdateCommand.getShowName();
        this.appName = applicationUpdateCommand.getAppName();
        log.info("update application: {}, appKey: {}", this.getAppName(), applicationUpdateCommand.getAppKey());
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
