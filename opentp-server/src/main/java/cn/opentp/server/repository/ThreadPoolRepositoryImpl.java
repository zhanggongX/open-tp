package cn.opentp.server.repository;

import cn.opentp.core.thread.pool.ThreadPoolState;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.connection.ConnectionImpl;
import cn.opentp.server.domain.threadpool.ThreadPoolRepository;
import cn.opentp.server.network.receive.ThreadPoolReceiveService;
import com.google.common.collect.Table;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Singleton
public class ThreadPoolRepositoryImpl implements ThreadPoolRepository {

    private final OpentpApp opentpApp = OpentpApp.instance();

    @Override
    public List<String> findByIpAndPid(String ipAndPid) {
        ThreadPoolReceiveService threadPoolReceiveService = opentpApp.receiveService();
        Table<ConnectionImpl, String, ThreadPoolState> connectThreadPoolStateTable = threadPoolReceiveService.connectThreadPoolStateTable();
        String[] ipAndPidVal = ipAndPid.split("-");
        ConnectionImpl connection = new ConnectionImpl(ipAndPidVal[0], ipAndPidVal[1]);

        Map<String, ThreadPoolState> row = connectThreadPoolStateTable.row(connection);
        return new ArrayList<>(row.keySet());
    }

    @Override
    public ThreadPoolState info(String ipAndPid, String tpName) {
        ThreadPoolReceiveService threadPoolReceiveService = opentpApp.receiveService();
        Table<ConnectionImpl, String, ThreadPoolState> connectThreadPoolStateTable = threadPoolReceiveService.connectThreadPoolStateTable();

        String[] ipAndPidVal = ipAndPid.split("-");
        ConnectionImpl connection = new ConnectionImpl(ipAndPidVal[0], ipAndPidVal[1]);
        return connectThreadPoolStateTable.get(connection, tpName);
    }
}
