package cn.opentp.server.domain.application;

import cn.opentp.server.domain.DomainEvent;

public class ApplicationCreateEvent implements DomainEvent {

    private String appKey;
    private String manager;

    public ApplicationCreateEvent() {
    }

    public ApplicationCreateEvent(String appKey, String manager) {
        this.appKey = appKey;
        this.manager = manager;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }
}
