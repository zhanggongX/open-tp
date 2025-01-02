package cn.opentp.server.domain;

import cn.opentp.server.domain.application.ApplicationCreateCommandHandler;
import cn.opentp.server.service.ManagerService;
import cn.opentp.server.service.ManagerServiceImpl;
import cn.opentp.server.service.domain.DefaultDomainCommandInvoker;
import cn.opentp.server.service.domain.DefaultDomainEventDispatcher;
import cn.opentp.server.service.domain.DomainCommandInvoker;
import cn.opentp.server.service.domain.DomainEventDispatcher;
import cn.opentp.server.service.event.ApplicationEventListener;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;


public class GuiceDomainModule extends AbstractModule {

    @Override
    protected void configure() {
//        bind(DomainCommandHandler.class).to(ApplicationCreateCommandHandler.class);
    }
}
