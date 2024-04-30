package opentp.client.spring.boot.starter.support;

import cn.opentp.client.configuration.Configuration;
import cn.opentp.client.constant.OpentpClientConstant;
import cn.opentp.client.exception.ServerAddrUnDefineException;
import cn.opentp.core.constant.OpentpCoreConstant;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ServerAddressParser {
    public static List<InetSocketAddress> parse(String servers) {

        String[] serverList = servers.split(OpentpClientConstant.SERVER_SPLITTER);
        if (serverList.length == 0) {
            throw new ServerAddrUnDefineException();
        }

        List<InetSocketAddress> inetSocketAddresses = new ArrayList<>();
        for (String server : serverList) {
            String[] serverAndPort = server.split(OpentpClientConstant.SERVER_PORT_SPLITTER);
            if (serverAndPort.length <= 1) {
                inetSocketAddresses.add(new InetSocketAddress(serverAndPort[0], OpentpClientConstant.DEFAULT_PORT));
            } else {
                inetSocketAddresses.add(new InetSocketAddress(serverAndPort[0], Integer.parseInt(serverAndPort[1])));
            }
        }

        return inetSocketAddresses;
    }
}
