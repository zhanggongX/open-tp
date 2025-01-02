package cn.opentp.server.domain.connect;

import cn.opentp.server.OpentpApp;
import cn.opentp.server.domain.DomainCommandHandler;
import cn.opentp.server.domain.DomainException;
import cn.opentp.server.domain.EventQueue;
import cn.opentp.server.domain.application.ApplicationImpl;
import cn.opentp.server.domain.application.ApplicationRepository;
import cn.opentp.server.network.receive.ThreadPoolReceiveService;
import com.google.inject.Inject;

public class ConnectCommandHandler implements DomainCommandHandler<EventQueue, ConnectCommand> {

    @Inject
    private ConnectRepository connectRepository;
    @Inject
    private ApplicationRepository applicationRepository;

    private final OpentpApp opentpApp = OpentpApp.instance();
    private final ThreadPoolReceiveService threadPoolReceiveService = opentpApp.receiveService();

    @Override
    public boolean handle(EventQueue eventQueue, ConnectCommand command) {
        ApplicationImpl application = applicationRepository.queryByKey(command.getAppKey());
        if(!application.getAppKey().equals(command.getAppKey()) || !application.getAppSecret().equals(command.getAppSecret())){
            throw new DomainException("auth fail");
        }

        ConnectImpl connect = connectRepository.buildConnect(command);
        connect.handle(eventQueue, command);
//        threadPoolReceiveService.
        return false;
    }
}
