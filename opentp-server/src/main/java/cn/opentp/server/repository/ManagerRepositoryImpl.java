package cn.opentp.server.repository;

import cn.opentp.core.util.JacksonUtil;
import cn.opentp.server.domain.DomainException;
import cn.opentp.server.domain.manager.*;
import cn.opentp.server.repository.rocksdb.OpentpRocksDB;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ManagerRepositoryImpl implements ManagerRepository {

    @Inject
    private OpentpRocksDB opentpRocksDB;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private boolean checkManagerRegistered(ManagerRegCommand command) {
        String managerInfo = opentpRocksDB.get(command.getUsername());
        return managerInfo != null && !managerInfo.isEmpty();
    }

    @Override
    public ManagerImpl checkOrGenerate(ManagerRegCommand command) {
        if (checkManagerRegistered(command)) {
            throw new DomainException("该用户已注册");
        } else {
            return new ManagerImpl(command.getUsername(), command.getPassword());
        }
    }

    @Override
    public boolean save(Manager manager) {
        if (manager instanceof ManagerImpl managerImpl) {
            opentpRocksDB.set(managerImpl.getUsername(), JacksonUtil.toJSONString(managerImpl));
        } else {
            throw new UnsupportedOperationException(manager.getClass().getName());
        }
        return true;
    }

    @Override
    public boolean checkRegisterAndPassword(ManagerLoginCommand command) {
        String managerInfo = opentpRocksDB.get(command.getUsername());
        if (managerInfo.isEmpty()) {
            throw new DomainException("该用户未注册");
        }
        ManagerImpl manager = JacksonUtil.parseJson(managerInfo, ManagerImpl.class);
        return manager.getPassword().equals(command.getPassword());
    }

    @Override
    public ManagerImpl checkAndBuildManger(ManagerChangeCommand command) {

        String managerInfo = opentpRocksDB.get(command.getUsername());
        if (managerInfo.isEmpty()) {
            throw new DomainException("该用户未注册");
        }
        ManagerImpl manager = JacksonUtil.parseJson(managerInfo, ManagerImpl.class);
//        log.info("manager : {}", manager);
        return manager;
    }

    @Override
    public ManagerImpl queryManagerInfo(String username) {
        String managerInfo = opentpRocksDB.get(username);
        if (managerInfo.isEmpty()) {
            throw new DomainException("该用户未注册");
        }
        return JacksonUtil.parseJson(managerInfo, ManagerImpl.class);
    }
}
