package cn.opentp.server.domain.application;

import cn.opentp.server.domain.DomainCommand;

/**
 * 应用存储层接口，领域层只提供接口，持久层提供实现。
 *
 * @author zg
 */
public interface ApplicationRepository {

    /**
     * 校验或者生成应用
     *
     * @param command 注册应用
     * @return application
     */
    Application checkOrGenerate(ApplicationCreateCommand command);

    void save(Application application);

    ApplicationImpl queryByKey(String appName);

    ApplicationImpl findOrError(DomainCommand command);

    void delete(Application application);
}
