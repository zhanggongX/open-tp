package cn.opentp.server.domain.application;

import cn.opentp.server.domain.DomainCommand;

/**
 * application register command
 *
 * @author zg
 */
public class ApplicationCreateCommand implements DomainCommand {

    /**
     * 应用名
     */
    private String appName;
    /**
     * 显示名
     */
    private String showName;

    public ApplicationCreateCommand(String appName, String showName) {
        this.appName = appName;
        this.showName = showName;
    }

    public ApplicationCreateCommand() {
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
}
