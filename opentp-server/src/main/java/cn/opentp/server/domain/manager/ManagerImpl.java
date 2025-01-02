package cn.opentp.server.domain.manager;

import cn.opentp.server.domain.EventQueue;

import java.util.List;

/**
 * 管理员
 */
public class ManagerImpl implements Manager {

    private String username;
    private String password;
    /**
     * '' | '*' | 'admin' | 'user';
     */
    private String role;
    private List<String> applications;

    public ManagerImpl() {
    }

    public ManagerImpl(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public ManagerImpl(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
