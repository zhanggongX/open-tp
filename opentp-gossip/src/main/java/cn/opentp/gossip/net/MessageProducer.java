package cn.opentp.gossip.net;


import java.io.IOException;

public interface MessageProducer
{
    public Message getMessage() throws IOException;
}
