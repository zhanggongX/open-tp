package cn.opentp.server.service.domain;

import cn.opentp.server.domain.DomainCommand;
import cn.opentp.server.domain.DomainCommandHandler;
import cn.opentp.server.domain.EventQueue;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultDomainCommandInvoker implements DomainCommandInvoker {

    private final DomainEventDispatcher domainEventDispatcher;

    @Inject
    public DefaultDomainCommandInvoker(DomainEventDispatcher domainEventDispatcher) {
        this.domainEventDispatcher = domainEventDispatcher;
    }

    @Override
    public boolean invoke(DomainCommand domainCommand, DomainCommandHandler<EventQueue, DomainCommand> function) {
        EventQueue queue = new DefaultEventQueue();
        boolean r = function.handle(queue, domainCommand);
        domainEventDispatcher.dispatch(queue);
        return r;
    }
}
