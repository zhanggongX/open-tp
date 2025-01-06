package cn.opentp.server.domain.application;

import cn.opentp.server.domain.EventQueue;

public interface Application {

    void handle(EventQueue eventQueue, ApplicationCreateCommand command);

    void checkConnect(String appKey, String appSecret);

    void handle(EventQueue eventQueue, ApplicationDeleteCommand command);

    void handle(EventQueue eventQueue, ApplicationUpdateCommand command);
}
