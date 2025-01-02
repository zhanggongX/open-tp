package cn.opentp.server.service;

import cn.opentp.server.domain.manager.ManagerImpl;
import cn.opentp.server.domain.manager.ManagerRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ManagerServiceImpl implements ManagerService {

    @Inject
    private ManagerRepository managerRepository;

    @Override
    public ManagerImpl queryManagerInfo(String username) {
        ManagerImpl manager = managerRepository.queryManagerInfo(username);
        manager.setPassword(null);
        return manager;
    }
}
