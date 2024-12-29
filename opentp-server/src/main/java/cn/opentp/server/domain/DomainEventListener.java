package cn.opentp.server.domain;

/**
 * 领域事件监听器
 */
public interface DomainEventListener {

    /**
     * 执行领域事件
     *
     * @param event 事件
     */
    void onEvent(DomainEvent event);
}
