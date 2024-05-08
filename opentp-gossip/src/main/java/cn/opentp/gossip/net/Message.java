package cn.opentp.gossip.net;

import java.net.InetSocketAddress;


public class Message
{
    final Header header_;
    private final byte[] body_;

    public Message(Header header, byte[] body)
    {
        assert header != null;
        assert body != null;

        header_ = header;
        body_ = body;
    }

    public Message(InetSocketAddress from, MessageVerb.Verb verb, byte[] body)
    {
        this(new Header(from, verb), body);
    }

  
    public byte[] getMessageBody()
    {
        return body_;
    }


    public InetSocketAddress getFrom()
    {
        return header_.getFrom();
    }


    public MessageVerb.Verb getVerb()
    {
        return header_.getVerb();
    }


    public String toString()
    {
        StringBuilder sbuf = new StringBuilder("");
        String separator = System.getProperty("line.separator");
        sbuf.append("FROM:" + getFrom())
        	.append(separator)
        	.append("VERB:" + getVerb())
        	.append(separator);
        return sbuf.toString();
    }
}
