package cn.opentp.server.domain.application;

import cn.opentp.server.domain.EventQueue;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ApplicationUpdateCommandHandler {

    @Inject
    private ApplicationRepository applicationRepository;

    public Boolean handle(EventQueue eventQueue, ApplicationUpdateCommand applicationUpdateCommand) {
        ApplicationImpl application = applicationRepository.findOrError(applicationUpdateCommand);
        application.handle(eventQueue, applicationUpdateCommand);
        applicationRepository.update(application, applicationUpdateCommand.getShowName(), applicationUpdateCommand.getAppName());
        return true;
    }
}
