package cn.opentp.server.domain.application;

import cn.opentp.server.domain.manager.ManagerRegCommand;

/**
 * application repository
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
    Application checkOrGenerate(ApplicationRegCommand command);

    void save(Application application);
}
