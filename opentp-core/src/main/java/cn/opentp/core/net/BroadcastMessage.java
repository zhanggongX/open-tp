package cn.opentp.core.net;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.auth.ServerInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 广播消息
 */
public class BroadcastMessage implements Serializable {

    private ServerInfo serverInfo;
    private List<ClientInfo> clientInfos;

    public List<ClientInfo> getClientInfos() {
        return clientInfos;
    }

    public void setClientInfos(List<ClientInfo> clientInfos) {
        this.clientInfos = clientInfos;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }
}
