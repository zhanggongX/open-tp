package cn.opentp.server.domain.application;

import cn.opentp.server.domain.DomainEvent;

public class ApplicationCreateEvent implements DomainEvent {

    private String appName;
    private String manager;

    public ApplicationCreateEvent() {
    }

    public ApplicationCreateEvent(String appName, String manager) {
        this.appName = appName;
        this.manager = manager;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }
}
