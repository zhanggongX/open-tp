package cn.opentp.client.network.keepr.strategy;

/**
 * 重连策略
 */
@FunctionalInterface
public interface ReconnectStrategy {

    void doConnect();
}
