package cn.opentp.client.net.keepr;

import cn.opentp.client.net.keepr.strategy.ExponentialStrategy;
import cn.opentp.client.net.keepr.strategy.ReconnectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * todo
 */
public class ReconnectTemplate {

    private static final Logger log = LoggerFactory.getLogger(ReconnectTemplate.class);

    private final ReconnectStrategy reconnectStrategy;

    public ReconnectTemplate() {
        this.reconnectStrategy = new ExponentialStrategy();
    }

    public ReconnectTemplate(ReconnectStrategy reconnectStrategy) {
        this.reconnectStrategy = reconnectStrategy;
    }

    public void reconnect() {
        reconnectStrategy.doConnect();
    }
}
