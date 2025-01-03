package cn.opentp.server.service.domain;

import cn.opentp.server.domain.EventQueue;

/**
 * 领域事件执行器
 */
public interface DomainCommandInvoker {

    <R> R invoke(CommandRunnable<R> runnable);

    @FunctionalInterface
    interface CommandRunnable<R> {
        R run(EventQueue eventQueue);
    }
}
