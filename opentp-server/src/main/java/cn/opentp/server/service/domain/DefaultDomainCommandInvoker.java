package cn.opentp.server.service.domain;

import cn.opentp.server.domain.DomainCommand;
import cn.opentp.server.domain.DomainCommandHandler;
import cn.opentp.server.domain.EventQueue;
import cn.opentp.server.domain.manager.ManagerRegCommand;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.function.BiFunction;
import java.util.function.Function;

@Singleton
public class DefaultDomainCommandInvoker implements DomainCommandInvoker {

    @Inject
    private DomainEventDispatcher domainEventDispatcher;

    @Override
    public <R> R invoke(DomainCommand domainCommand, DomainCommandHandler<EventQueue, DomainCommand, R> function) {
        EventQueue queue = new DefaultEventQueue();
        R r = function.handle(queue, domainCommand);
        domainEventDispatcher.dispatch(queue);
        return r;
    }
}
