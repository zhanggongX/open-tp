package cn.opentp.server.domain.manager;

import cn.opentp.server.domain.DomainCommandHandler;
import cn.opentp.server.domain.EventQueue;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ManagerChangeCommandHandler implements DomainCommandHandler<EventQueue, ManagerChangeCommand> {

    @Inject
    private ManagerRepository managerRepository;

    @Override
    public boolean handle(EventQueue eventQueue, ManagerChangeCommand command) {
        ManagerImpl manager = managerRepository.checkAndBuildManger(command);
        manager.handle(eventQueue, command);
        return managerRepository.save(manager);
    }
}
