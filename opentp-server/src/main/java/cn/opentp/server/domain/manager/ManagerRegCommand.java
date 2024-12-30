package cn.opentp.server.domain.manager;

import cn.opentp.server.domain.DomainCommand;

/**
 * 管理元注册命令
 *
 * @author zg
 */
public class ManagerRegCommand implements DomainCommand {

    private String userName;
    private String password;

    public ManagerRegCommand() {
    }

    public ManagerRegCommand(String userName, String password) {
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
}
