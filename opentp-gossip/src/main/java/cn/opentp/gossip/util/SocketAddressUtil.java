package cn.opentp.gossip.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class SocketAddressUtil {

    public static InetSocketAddress parseSocketAddress(String address) throws UnknownHostException {
        String[] addressInfo = address.split(":");
        if (addressInfo.length == 2) {
            return new InetSocketAddress(InetAddress.getByName(addressInfo[0]), Integer.parseInt(addressInfo[1]));
        } else {
            throw new UnknownHostException(address);
        }
    }
}