package cn.opentp.server.service.event;

import cn.opentp.server.domain.DomainEvent;
import cn.opentp.server.domain.DomainEventListener;
import cn.opentp.server.domain.application.ApplicationCreateEvent;
import cn.opentp.server.domain.manager.ManagerImpl;
import cn.opentp.server.domain.manager.ManagerRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

/**
 * 应用创建事件监听
 *
 * @author zg
 */
@Singleton
public class ApplicationEventListener implements DomainEventListener {

    @Inject
    private ManagerRepository managerRepository;

    @Override
    public void onEvent(DomainEvent event) {
        if (event instanceof ApplicationCreateEvent applicationCreateEvent) {
            ManagerImpl manager = managerRepository.queryManagerInfo(applicationCreateEvent.getManager());
            if (manager.getApplications() == null) {
                manager.setApplications(new ArrayList<>());
            }
            manager.getApplications().add(applicationCreateEvent.getAppName());
            managerRepository.save(manager);
        }
    }
}
