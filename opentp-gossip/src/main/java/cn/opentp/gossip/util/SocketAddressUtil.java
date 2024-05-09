package cn.opentp.gossip.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * 地址解析
 */
public class SocketAddressUtil {

    public static InetSocketAddress parseSocketAddress(String socketAddress) throws UnknownHostException {
        String[] socketAddressInfo = socketAddress.split(":");
        if (socketAddressInfo.length == 2) {
            return new InetSocketAddress(InetAddress.getByName(socketAddressInfo[0]),
                    Integer.parseInt(socketAddressInfo[1]));
        } else {
            throw new UnknownHostException(socketAddress);
        }
    }
}
