package cn.opentp.server.repository;

import cn.opentp.core.util.JacksonUtil;
import cn.opentp.server.domain.DomainCache;
import cn.opentp.server.domain.DomainException;
import cn.opentp.server.domain.application.*;
import cn.opentp.server.repository.rocksdb.OpentpRocksDB;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.UUID;

@Singleton
public class ApplicationRepositoryImpl implements ApplicationRepository {

    private static final String APPLICATION_KEY_PREFIX = "application:";

    @Inject
    OpentpRocksDB rocksDB;

    @Override
    public Application checkOrGenerate(ApplicationRegCommand command) {
        if (checkRegistered(command)) {
            throw new DomainException("该 Application 已被注册，不能再次注册");
        }

        ApplicationImpl application = new ApplicationImpl();
        application.setName(command.getName());
        application.setAppName(command.getAppName());
        application.setAppKey(generateApplicationKey());
        application.setAppSecret(generateAppSecret());

        return application;
    }

    private String generateApplicationKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String generateAppSecret() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void save(Application application) {
        if (application instanceof ApplicationImpl) {
            rocksDB.set(APPLICATION_KEY_PREFIX + ((ApplicationImpl) application).getAppName(), JacksonUtil.toJSONString(application));
        } else {
            throw new UnsupportedOperationException(application.getClass().getName());
        }
    }

    private boolean checkRegistered(ApplicationRegCommand command) {
        return rocksDB.exist(APPLICATION_KEY_PREFIX + command.getAppName());
    }
}
