package cn.opentp.server.domain.manager;

import cn.opentp.server.domain.DomainCommand;

/**
 * 管理元注册命令
 *
 * @author zg
 */
public class ManagerChangeCommand implements DomainCommand {

    private String username;
    private String password;
    private String newPassword;

    public ManagerChangeCommand() {
    }

    public ManagerChangeCommand(String username, String password, String newPassword) {
        this.username = username;
        this.password = password;
        this.newPassword = newPassword;
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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
