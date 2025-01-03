package cn.opentp.server.repository;

import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.connect.ConnectCommand;
import cn.opentp.server.domain.connect.ConnectImpl;
import cn.opentp.server.domain.connect.ConnectRepository;
import cn.opentp.server.network.receive.ThreadPoolReceiveService;
import com.google.inject.Singleton;

import java.util.ArrayList;

@Singleton
public class ConnectRepositoryImpl implements ConnectRepository {

    private final OpentpApp opentpApp = OpentpApp.instance();

    @Override
    public ConnectImpl buildConnect(ConnectCommand command) {
        return new ConnectImpl(command.getHost(), command.getPid(), command.getAppKey(), command.getAppSecret());
    }

    @Override
    public void save(ConnectImpl connect) {

        ThreadPoolReceiveService threadPoolReceiveService = opentpApp.receiveService();
        // 记录 appKey <-> 客户端信息
        threadPoolReceiveService.appKeyConnectCache().putIfAbsent(connect.getAppKey(), new ArrayList<>());
        threadPoolReceiveService.appKeyConnectCache().get(connect.getAppKey()).add(connect);

        // 记录 licenseKey <-> 客户端信息
//        threadPoolReceiveService.licenseClientCache().put(newLicenseKey, clientInfo);
//         记录 客户端信息 <-> 网络连接
//        threadPoolReceiveService.clientChannelCache().put(clientInfo, ctx.channel());
    }
}
