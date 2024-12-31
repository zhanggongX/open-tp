package cn.opentp.server.service;

import cn.opentp.server.domain.manager.ManagerImpl;

public interface ManagerService {

    ManagerImpl queryManagerInfo(String username);
}
