package cn.opentp.server.domain.manager;

import cn.opentp.server.domain.DomainCommandHandler;
import cn.opentp.server.domain.EventQueue;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ManagerLoginCommandHandler implements DomainCommandHandler<EventQueue, ManagerLoginCommand> {

    @Inject
    private ManagerRepository managerRepository;

    @Override
    public boolean handle(EventQueue eventQueue, ManagerLoginCommand command) {
        return managerRepository.checkRegisterAndPassword(command);
    }
}
