package cn.opentp.server.domain;

import cn.opentp.server.domain.manager.ManagerRegCommand;
import cn.opentp.server.infrastructure.auth.LicenseKeyFactory;

@FunctionalInterface
public interface DomainCommandHandler<U extends EventQueue, T extends DomainCommand, R> {

    R handle(U eventQueue, T command);
}
