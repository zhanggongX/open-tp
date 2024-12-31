package cn.opentp.server.domain.manager;

import cn.opentp.server.domain.EventQueue;

import java.util.List;

/**
 * 管理员
 */
public class ManagerImpl implements Manager {

    private String userName;
    private String password;
    /**
     * '' | '*' | 'admin' | 'user';
     */
    private String role;
    private List<String> applications;

    public ManagerImpl() {
    }

    public ManagerImpl(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getApplications() {
        return applications;
    }

    public void setApplications(List<String> applications) {
        this.applications = applications;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void handle(EventQueue eventQueue, ManagerRegCommand command) {
        // don't do anything
    }

    @Override
    public void handle(EventQueue eventQueue, ManagerChangeCommand command) {
        this.password = command.getNewPassword();
    }
}
