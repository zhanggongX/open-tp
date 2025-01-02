package cn.opentp.server.domain.connect;

public interface ConnectRepository {

    ConnectImpl buildConnect(ConnectCommand command);
}
