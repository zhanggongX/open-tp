package cn.opentp.server.domain.manager;

import cn.opentp.server.domain.EventQueue;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ManagerRegCommandHandler {

    @Inject
    private ManagerRepository managerRepository;

    public boolean handle(EventQueue eventQueue, ManagerRegCommand command) {
        ManagerImpl manager = managerRepository.checkOrGenerate(command);
        manager.handle(eventQueue, command);
        return managerRepository.save(manager);
    }
}
