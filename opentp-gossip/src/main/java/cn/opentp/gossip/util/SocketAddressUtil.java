package cn.opentp.gossip.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class SocketAddressUtil {

    public static InetSocketAddress parseSocketAddress(String str) throws UnknownHostException {
        String[] strs = str.split(":");
        if (strs.length == 2) {
            return new InetSocketAddress(InetAddress.getByName(strs[0]),
                    Integer.parseInt(strs[1]));
        } else {
            throw new UnknownHostException(str);
        }
    }
}
