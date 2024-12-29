package cn.opentp.server.service.domain;

import cn.opentp.server.domain.DomainCommand;
import cn.opentp.server.domain.DomainCommandHandler;
import cn.opentp.server.domain.EventQueue;
import cn.opentp.server.domain.manager.ManagerRegCommand;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 领域事件执行器
 */
public interface DomainCommandInvoker {

    <R> R invoke(DomainCommand domainCommand, DomainCommandHandler<EventQueue, DomainCommand, R> function);

}
