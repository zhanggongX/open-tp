package cn.opentp.client.net.keepr.strategy;

/**
 * 重连策略
 */
@FunctionalInterface
public interface ReconnectStrategy {

    void doConnect();
}
