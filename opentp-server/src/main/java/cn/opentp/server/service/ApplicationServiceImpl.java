package cn.opentp.server.service;

import cn.opentp.server.domain.application.ApplicationImpl;
import cn.opentp.server.domain.application.ApplicationRepository;
import cn.opentp.server.domain.manager.ManagerImpl;
import cn.opentp.server.domain.manager.ManagerRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class ApplicationServiceImpl implements ApplicationService {

    @Inject
    private ApplicationRepository applicationRepository;
    @Inject
    private ManagerRepository managerRepository;

    @Override
    public List<ApplicationImpl> applications(String username) {
        ManagerImpl manager = managerRepository.queryManagerInfo(username);
        if (manager.getApplications() == null) {
            return List.of();
        }

        List<ApplicationImpl> applications = new ArrayList<>();
        for (String appKey : manager.getApplications()) {
            ApplicationImpl application = applicationRepository.queryByKey(appKey);
            if (application != null) {
                applications.add(application);
            }
        }

        return applications;
    }
}
