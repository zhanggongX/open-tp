package cn.opentp.server.domain.application;

import cn.opentp.server.domain.DomainEvent;

import java.util.List;

public class ApplicationDeleteEvent implements DomainEvent {

    private String appKey;
    private String appName;
    private List<String> managers;

    public ApplicationDeleteEvent() {
    }

    public ApplicationDeleteEvent(String appKey, String appName, List<String> managers) {
        this.appKey = appKey;
        this.appName = appName;
        this.managers = managers;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<String> getManagers() {
        return managers;
    }

    public void setManagers(List<String> managers) {
        this.managers = managers;
    }
}
