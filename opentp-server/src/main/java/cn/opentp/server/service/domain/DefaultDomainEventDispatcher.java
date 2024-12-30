package cn.opentp.server.service.domain;

import cn.opentp.server.domain.EventQueue;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultDomainEventDispatcher implements DomainEventDispatcher {

    @Override
    public void dispatch(EventQueue eventQueue) {

    }
}
