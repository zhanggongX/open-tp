package cn.opentp.server.repository;

import cn.opentp.core.util.JacksonUtil;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.DomainException;
import cn.opentp.server.domain.application.*;
import cn.opentp.server.repository.rocksdb.OpentpRocksDBImpl;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;
import java.util.UUID;

@Singleton
public class ApplicationRepositoryImpl implements ApplicationRepository {

    private static final String APPLICATION_KEY_PREFIX = "application:";

    @Inject
    OpentpRocksDBImpl rocksDB;

    private final OpentpApp opentpApp = OpentpApp.instance();

    @Override
    public Application checkOrGenerate(ApplicationCreateCommand command) {
        if (checkRegistered(command)) {
            throw new DomainException("该 Application 已被注册，不能再次注册");
        }

        ApplicationImpl application = new ApplicationImpl();
        application.setShowName(command.getShowName());
        application.setAppName(command.getAppName());
        application.setAppKey(generateApplicationKey());
        application.setAppSecret(generateAppSecret());
        application.setManagers(List.of(opentpApp.getUsername()));

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

    private boolean checkRegistered(ApplicationCreateCommand command) {
        return rocksDB.exist(APPLICATION_KEY_PREFIX + command.getAppName());
    }

    @Override
    public ApplicationImpl queryByName(String appName) {
        String applicationInfo = rocksDB.get(APPLICATION_KEY_PREFIX + appName);
        if (applicationInfo == null) {
            return null;
        }

        return JacksonUtil.parseJson(applicationInfo, ApplicationImpl.class);
    }
}
