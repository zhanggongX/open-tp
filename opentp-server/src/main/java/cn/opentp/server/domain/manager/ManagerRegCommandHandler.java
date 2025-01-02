package cn.opentp.server.domain.manager;

import cn.opentp.server.domain.DomainCommandHandler;
import cn.opentp.server.domain.EventQueue;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ManagerRegCommandHandler implements DomainCommandHandler<EventQueue, ManagerRegCommand> {

    @Inject
    private ManagerRepository managerRepository;

    @Override
    public boolean handle(EventQueue eventQueue, ManagerRegCommand command) {
        ManagerImpl manager = managerRepository.checkOrGenerate(command);
        manager.handle(eventQueue, command);
        return managerRepository.save(manager);
    }
}