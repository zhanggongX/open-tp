package cn.opentp.server.domain.connection;

import cn.opentp.server.domain.EventQueue;

public interface Connection {

    /**
     * 处理连接命令
     *
     * @param eventQueue 事件队列
     * @param command    连接命令
     * @return licenseKye
     */
    boolean handle(EventQueue eventQueue, ConnectCommand command);
}
