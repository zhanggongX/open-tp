package cn.opentp.gossip.util;

import cn.opentp.gossip.Gossiper;

import java.net.InetSocketAddress;

public class FBUtilities {

    private static volatile InetSocketAddress localInetAddress_;
    private static volatile InetSocketAddress broadcastInetAddress_;

    /**
     * Please use getBroadcastAddress instead. You need this only when you have to listen/connect.
     */
    public static InetSocketAddress getLocalAddress() {
        if (localInetAddress_ == null) {
            localInetAddress_ = Gossiper.getListenAddress();
        }
        return localInetAddress_;
    }

    public static InetSocketAddress getBroadcastAddress() {
        if (broadcastInetAddress_ == null)
            broadcastInetAddress_ = Gossiper.getBroadcastAddress() == null
                    ? getLocalAddress()
                    : Gossiper.getBroadcastAddress();
        return broadcastInetAddress_;
    }


    public static int encodedUTF8Length(String st) {
        int strlen = st.length();
        int utflen = 0;
        for (int i = 0; i < strlen; i++) {
            int c = st.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F))
                utflen++;
            else if (c > 0x07FF)
                utflen += 3;
            else
                utflen += 2;
        }
        return utflen;
    }
}