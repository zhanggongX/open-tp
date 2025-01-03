package cn.opentp.server.domain.connect;

import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.EventQueue;
import cn.opentp.server.domain.application.ApplicationImpl;
import cn.opentp.server.domain.application.ApplicationRepository;
import cn.opentp.server.network.receive.ThreadPoolReceiveService;
import com.google.inject.Inject;

public class ConnectCommandHandler {

    @Inject
    private ConnectRepository connectRepository;
    @Inject
    private ApplicationRepository applicationRepository;

    private final OpentpApp opentpApp = OpentpApp.instance();
    private final ThreadPoolReceiveService threadPoolReceiveService = opentpApp.receiveService();

    public Boolean handle(EventQueue eventQueue, ConnectCommand command) {
        ApplicationImpl application = applicationRepository.queryByKey(command.getAppKey());
        application.checkConnect(command.getAppKey(), command.getAppSecret());

        ConnectImpl connect = connectRepository.buildConnect(command);
        connect.handle(eventQueue, command);
        connectRepository.save(connect);
        return true;
    }
}
