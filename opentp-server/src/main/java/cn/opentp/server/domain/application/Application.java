package cn.opentp.server.domain.application;

import cn.opentp.server.domain.EventQueue;

public interface Application {

    void handle(EventQueue eventQueue, ApplicationCreateCommand command);
}
