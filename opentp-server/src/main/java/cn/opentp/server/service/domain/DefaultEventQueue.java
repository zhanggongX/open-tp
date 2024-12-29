package cn.opentp.server.service.domain;

import cn.opentp.server.domain.DomainEvent;
import cn.opentp.server.domain.EventQueue;
import com.google.inject.Singleton;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * opentp 事件队列
 * 复杂业务可以使用消息队列中间件。
 *
 * @author zg
 */
@Singleton
public class DefaultEventQueue implements EventQueue {

    private final Queue<DomainEvent> queue = new ConcurrentLinkedQueue<>();

    @Override
    public void offer(DomainEvent domainEvent) {
        queue.offer(domainEvent);
    }

    @Override
    public Queue<DomainEvent> queue() {
        return queue;
    }
}
