package cn.opentp.server.service;

import cn.opentp.server.domain.DomainEventListener;
import cn.opentp.server.service.domain.DefaultDomainCommandInvoker;
import cn.opentp.server.service.domain.DefaultDomainEventDispatcher;
import cn.opentp.server.service.domain.DomainCommandInvoker;
import cn.opentp.server.service.domain.DomainEventDispatcher;
import cn.opentp.server.service.event.ApplicationEventListener;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;


public class GuiceServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DomainEventDispatcher.class).to(DefaultDomainEventDispatcher.class);
        bind(DomainCommandInvoker.class).to(DefaultDomainCommandInvoker.class);

        Multibinder<DomainEventListener> multibinder = Multibinder.newSetBinder(binder(), DomainEventListener.class);
        multibinder.addBinding().to(ApplicationEventListener.class);

        bind(ManagerService.class).to(ManagerServiceImpl.class);
        bind(ApplicationService.class).to(ApplicationServiceImpl.class);
        bind(ThreadPoolService.class).to(ThreadPoolServiceImpl.class);
    }
}
