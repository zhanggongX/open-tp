package cn.opentp.server.domain.application;

import cn.opentp.server.domain.DomainCommand;

public class ApplicationDeleteCommand implements DomainCommand {

    private String appKey;

    public ApplicationDeleteCommand() {

    }

    public ApplicationDeleteCommand(String appKey) {
        this.appKey = appKey;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
