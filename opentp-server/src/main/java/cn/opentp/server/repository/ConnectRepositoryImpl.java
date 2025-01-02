package cn.opentp.server.repository;

import cn.opentp.server.domain.connect.ConnectCommand;
import cn.opentp.server.domain.connect.ConnectImpl;
import cn.opentp.server.domain.connect.ConnectRepository;
import com.google.inject.Singleton;

@Singleton
public class ConnectRepositoryImpl implements ConnectRepository {

    @Override
    public ConnectImpl buildConnect(ConnectCommand command) {
        return new ConnectImpl(command.getHost(), command.getPid(), command.getAppKey(), command.getAppSecret());
    }
}
