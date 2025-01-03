package cn.opentp.server.domain.manager;

import cn.opentp.server.domain.EventQueue;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ManagerLoginCommandHandler {

    @Inject
    private ManagerRepository managerRepository;

    public boolean handle(EventQueue eventQueue, ManagerLoginCommand command) {
        return managerRepository.checkRegisterAndPassword(command);
    }
}
