package cn.opentp.server.domain.connection;

import java.util.List;

public interface ConnectionRepository {

    ConnectionImpl buildConnect(ConnectCommand command);

    void save(ConnectionImpl connect);

    List<ConnectionImpl> findByAppKey(String appKey);
}
