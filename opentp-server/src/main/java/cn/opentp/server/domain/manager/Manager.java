package cn.opentp.server.domain.manager;

import cn.opentp.server.domain.EventQueue;

public interface Manager {

    void handle(EventQueue eventQueue, ManagerRegCommand command);

    void handle(EventQueue eventQueue, ManagerChangeCommand command);
}
