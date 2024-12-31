package cn.opentp.server.domain.manager;

import cn.opentp.server.domain.DomainCommand;

public interface ManagerRepository {

    ManagerImpl checkOrGenerate(ManagerRegCommand command);

    boolean checkRegisterAndPassword(ManagerLoginCommand command);

    boolean save(Manager manager);

    ManagerImpl checkAndBuildManger(ManagerChangeCommand command);

    ManagerImpl queryUserInfo(String username);
}
