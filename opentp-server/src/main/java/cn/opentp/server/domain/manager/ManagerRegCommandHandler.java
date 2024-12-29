package cn.opentp.server.domain.manager;

import cn.opentp.server.domain.DomainCommandHandler;
import cn.opentp.server.domain.EventQueue;
import com.google.inject.Inject;

public class ManagerRegCommandHandler implements DomainCommandHandler<EventQueue, ManagerRegCommand, Boolean> {

    @Inject
    private ManagerRepository managerRepository;

    @Override
    public Boolean handle(EventQueue eventQueue, ManagerRegCommand command) {
        managerRepository.checkManager(command);
        return true;
//        managerRepository.save(command);
    }
}
