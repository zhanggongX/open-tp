package cn.opentp.server.domain.application;

import cn.opentp.server.domain.DomainCommandHandler;
import cn.opentp.server.domain.EventQueue;
import com.google.inject.Inject;

/**
 * application registration command handler
 *
 * @author zg
 */
public class ApplicationRegCommandHandler implements DomainCommandHandler<ApplicationRegCommand> {

    @Inject
    private ApplicationRepository applicationRepository;

    @Override
    public void handle(EventQueue eventQueue, ApplicationRegCommand command) {
        Application application = applicationRepository.checkOrGenerate(command);
        application.handle(command);
        applicationRepository.save(application);
    }
}
