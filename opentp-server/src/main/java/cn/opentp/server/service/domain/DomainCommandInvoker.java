package cn.opentp.server.service.domain;

import cn.opentp.server.domain.DomainCommand;
import cn.opentp.server.domain.DomainCommandHandler;
import cn.opentp.server.domain.EventQueue;

/**
 * 领域事件执行器
 */
public interface DomainCommandInvoker {

    boolean invoke(DomainCommand domainCommand, DomainCommandHandler<EventQueue, DomainCommand> function);

}
