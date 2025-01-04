package cn.opentp.server.domain.application;

import cn.opentp.server.domain.EventQueue;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ApplicationDeleteCommandHandler {

    @Inject
    private ApplicationRepository applicationRepository;

    public Boolean handle(EventQueue eventQueue, ApplicationDeleteCommand command) {
        Application application = applicationRepository.findOrError(command);
        application.handle(eventQueue, command);
        applicationRepository.delete(application);
        return true;
    }
}
