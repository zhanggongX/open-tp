package cn.opentp.server.domain.application;

public interface Application {

    void handle(ApplicationRegCommand command);
}
