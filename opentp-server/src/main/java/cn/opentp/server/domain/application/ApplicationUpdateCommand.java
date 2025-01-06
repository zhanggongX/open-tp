package cn.opentp.server.domain.application;

import cn.opentp.server.domain.DomainCommand;

public class ApplicationUpdateCommand implements DomainCommand {

    private String showName;
    private String appName;
    private String appKey;

    public ApplicationUpdateCommand() {
    }

    public ApplicationUpdateCommand(String showName, String appName, String appKey) {
        this.showName = showName;
        this.appName = appName;
        this.appKey = appKey;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
