package cn.opentp.server.domain.connect;

import cn.opentp.server.domain.EventQueue;

public interface Connect {

    void handle(EventQueue eventQueue, ConnectCommand command);
}
