package cn.opentp.server.repository;

import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.connection.ConnectCommand;
import cn.opentp.server.domain.connection.ConnectionImpl;
import cn.opentp.server.domain.connection.ConnectionRepository;
import cn.opentp.server.network.receive.ThreadPoolReceiveService;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Singleton
public class ConnectionRepositoryImpl implements ConnectionRepository {

    private final OpentpApp opentpApp = OpentpApp.instance();

    @Override
    public ConnectionImpl buildConnect(ConnectCommand command) {
        return new ConnectionImpl(command.getHost(), command.getPid(), command.getAppKey(), command.getAppSecret());
    }

    @Override
    public void save(ConnectionImpl connect) {

        ThreadPoolReceiveService threadPoolReceiveService = opentpApp.receiveService();
        // 记录 appKey <-> 客户端信息
        threadPoolReceiveService.appKeyConnectCache().putIfAbsent(connect.getAppKey(), new ArrayList<>());
        threadPoolReceiveService.appKeyConnectCache().get(connect.getAppKey()).add(connect);

        // 记录 licenseKey <-> 客户端信息
//        threadPoolReceiveService.licenseClientCache().put(newLicenseKey, clientInfo);
//         记录 客户端信息 <-> 网络连接
//        threadPoolReceiveService.clientChannelCache().put(clientInfo, ctx.channel());
    }

    @Override
    public List<ConnectionImpl> findByAppKey(String appKey) {
        ThreadPoolReceiveService threadPoolReceiveService = opentpApp.receiveService();
        List<ConnectionImpl> connections = threadPoolReceiveService.appKeyConnectCache().get(appKey);
        return Objects.requireNonNullElseGet(connections, List::of);
    }
}
