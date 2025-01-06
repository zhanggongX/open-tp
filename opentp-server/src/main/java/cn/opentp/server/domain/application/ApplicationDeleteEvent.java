package cn.opentp.server.domain.application;

import cn.opentp.server.domain.DomainEvent;

import java.util.List;

public class ApplicationDeleteEvent implements DomainEvent {

    private String appKey;
    private List<String> managers;

    public ApplicationDeleteEvent() {
    }

    public ApplicationDeleteEvent(String appKey, List<String> managers) {
        this.appKey = appKey;
        this.managers = managers;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public List<String> getManagers() {
        return managers;
    }

    public void setManagers(List<String> managers) {
        this.managers = managers;
    }
}
