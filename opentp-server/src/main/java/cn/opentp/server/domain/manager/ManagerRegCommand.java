package cn.opentp.server.domain.manager;

import cn.opentp.server.domain.DomainCommand;

/**
 * manager register
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

}
