package cn.opentp.server.domain.application;

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

    /**
     * 校验管理员
     *
     * @param command 管理员注册命令
     */
    void checkManager(ManagerRegCommand command);
}
