package cn.opentp.server.domain;

@FunctionalInterface
public interface DomainCommandHandler<Q extends EventQueue, T extends DomainCommand> {

    boolean handle(Q eventQueue, T command);
}
