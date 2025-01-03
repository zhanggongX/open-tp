package cn.opentp.server.service.domain;

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
    public <R> R invoke(CommandRunnable<R> runnable) {
        EventQueue queue = new DefaultEventQueue();
        R r = runnable.run(queue);
        domainEventDispatcher.dispatch(queue);
        return r;
    }
}
