package cn.opentp.server.domain.threadpool;

import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.EventQueue;
import cn.opentp.server.domain.connect.ConnectImpl;
import cn.opentp.server.network.receive.ThreadPoolReceiveService;
import com.google.common.collect.Table;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ThreadPoolReportCommandHandler {

    private final Logger log = LoggerFactory.getLogger(ThreadPoolReportCommandHandler.class);

    public Boolean handle(EventQueue eventQueue, ThreadPoolReportCommand threadPoolReportCommand) {
        ThreadPoolReceiveService threadPoolReceiveService = OpentpApp.instance().receiveService();

        ConnectImpl connect = threadPoolReceiveService.connectChannelCache().inverse().get(threadPoolReportCommand.getChannel());

        // 刷新线程池信息
        Table<ConnectImpl, String, ThreadPoolState> connectStringThreadPoolStateTable = threadPoolReceiveService.connectThreadPoolStateTable();
        for (ThreadPoolState reportThreadPoolState : threadPoolReportCommand.getThreadPoolStates()) {

            ThreadPoolState threadPoolState = connectStringThreadPoolStateTable.get(connect, reportThreadPoolState.getThreadPoolName());
            if (threadPoolState == null) {
                threadPoolState = new ThreadPoolState();
            }

            threadPoolState.flushState(reportThreadPoolState);

            log.debug("上报线程池信息 : {}", threadPoolState);
        }
        return true;
    }
}
