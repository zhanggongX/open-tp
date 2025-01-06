package cn.opentp.server.repository;

import cn.opentp.core.util.JacksonUtil;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.DomainCommand;
import cn.opentp.server.domain.DomainException;
import cn.opentp.server.domain.application.*;
import cn.opentp.server.repository.rocksdb.OpentpRocksDBImpl;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.UUID;

@Singleton
public class ApplicationRepositoryImpl implements ApplicationRepository {

    private static final String APPLICATION_KEY_PREFIX = "applicationKey:";

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
        application.setManagers(List.of(opentpApp.getManagerUsername()));

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
        if (application instanceof ApplicationImpl applicationImpl) {
            rocksDB.set(APPLICATION_KEY_PREFIX + applicationImpl.getAppKey(), JacksonUtil.toJSONString(applicationImpl));
        } else {
            throw new UnsupportedOperationException(application.getClass().getName());
        }
    }

    private boolean checkRegistered(ApplicationCreateCommand command) {
        return rocksDB.exist(APPLICATION_KEY_PREFIX + command.getAppName());
    }

    @Override
    public ApplicationImpl queryByKey(String appName) {
        String applicationInfo = rocksDB.get(APPLICATION_KEY_PREFIX + appName);
        if (applicationInfo.isEmpty()) {
            return null;
        }

        return JacksonUtil.parseJson(applicationInfo, ApplicationImpl.class);
    }

    @Override
    public ApplicationImpl findOrError(DomainCommand command) {
        String applicationInfo = Strings.EMPTY;
        String appKey = Strings.EMPTY;

        if (command instanceof ApplicationDeleteCommand applicationDeleteCommand) {
            applicationInfo = rocksDB.get(APPLICATION_KEY_PREFIX + applicationDeleteCommand.getAppKey());
            appKey = applicationDeleteCommand.getAppKey();
        } else if (command instanceof ApplicationUpdateCommand applicationUpdateCommand) {
            applicationInfo = rocksDB.get(APPLICATION_KEY_PREFIX + applicationUpdateCommand.getAppKey());
            appKey = applicationUpdateCommand.getAppKey();
        }

        if (applicationInfo.isEmpty()) {
            throw new DomainException("appKey[ " + appKey + "]不存在");
        }
        return JacksonUtil.parseJson(applicationInfo, ApplicationImpl.class);
    }

    @Override
    public void delete(Application application) {
        if (application instanceof ApplicationImpl applicationImpl) {
            rocksDB.delete(APPLICATION_KEY_PREFIX + applicationImpl.getAppKey());
        }
    }

    @Override
    public void update(Application application, String showName, String appName) {
        if (application instanceof ApplicationImpl applicationImpl) {
            rocksDB.set(APPLICATION_KEY_PREFIX + applicationImpl.getAppKey(), JacksonUtil.toJSONString(applicationImpl));
        }
    }
}
