package cn.opentp.server.domain.manager;

import cn.opentp.server.domain.DomainCommandHandler;
import cn.opentp.server.domain.EventQueue;
import com.google.inject.Inject;

public class ManagerRegCommandHandler implements DomainCommandHandler<EventQueue, ManagerRegCommand> {

    @Inject
    private ManagerRepository managerRepository;

    @Override
    public boolean handle(EventQueue eventQueue, ManagerRegCommand command) {
        managerRepository.checkManager(command);
        return true;
//        managerRepository.save(command);
    }
}
