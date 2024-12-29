package cn.opentp.server.service.domain;

import cn.opentp.server.domain.EventQueue;

/**
 * 领域事件分发器
 */
public interface DomainEventDispatcher {

    void dispatch(EventQueue eventQueue);
}
