package cn.opentp.server.domain;

import java.util.List;
import java.util.Queue;

/**
 * 事件队列
 */
public interface EventQueue {

    /**
     * 添加事件
     *
     * @param domainEvent 领域事件
     */
    void offer(DomainEvent domainEvent);

    /**
     * 获取事件队列
     *
     * @return 事件队列
     */
    Queue<DomainEvent> queue();
}
