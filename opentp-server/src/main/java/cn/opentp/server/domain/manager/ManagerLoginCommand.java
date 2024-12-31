package cn.opentp.server.domain.manager;

import cn.opentp.server.domain.DomainCommand;


public class ManagerLoginCommand implements DomainCommand {

    private String username;
    private String password;

    public ManagerLoginCommand() {
    }

    public ManagerLoginCommand(String username, String password) {
        this.username = username;
        this.password = password;
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
}
