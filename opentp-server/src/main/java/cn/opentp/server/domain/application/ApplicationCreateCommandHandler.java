package cn.opentp.server.domain.application;

import cn.opentp.server.domain.DomainCommandHandler;
import cn.opentp.server.domain.EventQueue;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 应用创建 command 处理
 *
 * @author zg
 */
@Singleton
public class ApplicationCreateCommandHandler implements DomainCommandHandler<EventQueue, ApplicationCreateCommand> {

    @Inject
    private ApplicationRepository applicationRepository;

    @Override
    public boolean handle(EventQueue eventQueue, ApplicationCreateCommand command) {
        Application application = applicationRepository.checkOrGenerate(command);
        application.handle(eventQueue, command);
        applicationRepository.save(application);
        return true;
    }
}
