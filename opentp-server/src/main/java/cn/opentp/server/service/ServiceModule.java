package cn.opentp.server.service;

import cn.opentp.server.service.domain.DefaultDomainCommandInvoker;
import cn.opentp.server.service.domain.DefaultDomainEventDispatcher;
import cn.opentp.server.service.domain.DomainCommandInvoker;
import cn.opentp.server.service.domain.DomainEventDispatcher;
import com.google.inject.AbstractModule;

public class ServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DomainEventDispatcher.class).to(DefaultDomainEventDispatcher.class);
        bind(DomainCommandInvoker.class).to(DefaultDomainCommandInvoker.class);
    }
}
