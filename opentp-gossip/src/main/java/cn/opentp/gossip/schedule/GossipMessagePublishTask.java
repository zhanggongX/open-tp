package cn.opentp.gossip.schedule;

import cn.opentp.gossip.GossipEnvironment;
import cn.opentp.gossip.message.GossipMessage;
import cn.opentp.gossip.message.codec.GossipMessageCodec;
import cn.opentp.gossip.message.holder.GossipMessageHolder;
import cn.opentp.gossip.util.GossipUtil;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class GossipMessagePublishTask extends AbstractGossipTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(GossipMessagePublishTask.class);

    @Override
    public void run() {
//        GossipEnvironment environment = GossipEnvironment.instance();
//        GossipMessageHolder messageHolder = environment.gossipMessageHolder();
//        if (messageHolder.isEmpty()) {
//            return;
//        }
//
//        try {
//            Set<String> messageIds = messageHolder.list();
//            for (String messageId : messageIds) {
//                GossipMessage message = messageHolder.acquire(messageId);
//                int forwardCount = message.getForwardCount();
//                int maxTry = GossipUtil.fanOut();
//                if (forwardCount < maxTry) {
//                    ByteBuf byteBuf = GossipMessageCodec.codec().encodeGossipMessage(message);
//                    sendBuf(byteBuf);
//                    message.setForwardCount(forwardCount + 1);
//                }
//                // 如果过了有效时间，还没有向 fan-out 的节点发送出去，则不再发送。
//                if ((System.currentTimeMillis() - message.getCreateTime()) >= message.getEffectTime()) {
//                    messageHolder.remove(messageId);
//                }
//            }
//        } catch (Exception e) {
//            log.error("发送流言信息出现异常: ", e);
//        }
    }
}
