package cn.opentp.server.service.domain;

import cn.opentp.server.domain.DomainEventListener;
import cn.opentp.server.domain.EventQueue;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Set;

@Singleton
public class DefaultDomainEventDispatcher implements DomainEventDispatcher {

    @Inject
    private Set<DomainEventListener> domainEventListeners;

    @Override
    public void dispatch(EventQueue eventQueue) {
        eventQueue.queue().forEach(event -> {
            domainEventListeners.forEach(listener -> listener.onEvent(event));
        });
    }
}
