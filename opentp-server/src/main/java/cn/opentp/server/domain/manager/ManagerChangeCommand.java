package cn.opentp.server.domain.manager;

import cn.opentp.server.domain.DomainCommand;

/**
 * 管理元注册命令
 *
 * @author zg
 */
public class ManagerChangeCommand implements DomainCommand {

    private String userName;
    private String password;
    private String newPassword;

    public ManagerChangeCommand() {
    }

    public ManagerChangeCommand(String userName, String password, String newPassword) {
        this.userName = userName;
        this.password = password;
        this.newPassword = newPassword;
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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
