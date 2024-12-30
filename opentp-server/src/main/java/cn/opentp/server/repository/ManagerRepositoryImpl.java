package cn.opentp.server.repository;

import cn.opentp.server.domain.manager.ManagerRegCommand;
import cn.opentp.server.domain.manager.ManagerRepository;
import com.google.inject.Singleton;

@Singleton
public class ManagerRepositoryImpl implements ManagerRepository {
    @Override
    public void checkManager(ManagerRegCommand command) {

    }
}
