package cn.opentp.server.network.receive.message.handler;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.auth.License;
import cn.opentp.core.constant.OpentpCoreConstant;
import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.core.net.serializer.SerializerTypeEnum;
import cn.opentp.server.OpentpApp;
import cn.opentp.server.infrastructure.auth.LicenseKeyFactory;
import cn.opentp.server.network.receive.ThreadPoolReceiveService;
import cn.opentp.server.repository.rocksdb.OpentpRocksDBImpl;
import com.google.inject.Inject;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * 权限认证消息
 */
public class AuthMessageHandler implements MessageHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    private OpentpRocksDBImpl opentpRocksDB;

    /**
     * 处理客户端连接信息
     *
     * @param ctx           channelHandler环境
     * @param opentpMessage 消息内容
     */
    @Override
    public void handle(ThreadPoolReceiveService service, ChannelHandlerContext ctx, OpentpMessage opentpMessage) {
        ClientInfo clientInfo = (ClientInfo) opentpMessage.getData();
        clientInfo.setServerInfo(OpentpApp.instance().selfInfo());

        log.debug("有新认证到来，appKey: {}, appSecret: {}, host: {}, instance: {}", clientInfo.getAppKey(), clientInfo.getAppSecret(), clientInfo.getHost(), clientInfo.getInstance());
        // todo 认证消息动态
        if (clientInfo.getAppKey() == null) {
            log.warn("新认证到来，未知的 appId : {}", clientInfo.getAppKey());
            ctx.channel().close();
            return;
        }
        String appSecret = opentpRocksDB.get(clientInfo.getAppKey());
        if (clientInfo.getAppSecret() == null || !clientInfo.getAppSecret().equals(appSecret)) {
            log.warn("新认证到来, appId : {}, appSecret error ", clientInfo.getAppKey());
            ctx.channel().close();
            return;
        }

        String newLicenseKey = LicenseKeyFactory.get();
        log.debug("新链接认证成功：返回 licenseKey : {}", newLicenseKey);

        // 设置 licenseKey
        ctx.channel().attr(OpentpCoreConstant.EXPORT_CHANNEL_ATTR_KEY).set(newLicenseKey);

        // 记录 appKey <-> 客户端信息
        service.appKeyClientCache().putIfAbsent(clientInfo.getAppKey(), new ArrayList<>());
        service.appKeyClientCache().get(clientInfo.getAppKey()).add(clientInfo);

        // 记录 licenseKey <-> 客户端信息
        service.licenseClientCache().put(newLicenseKey, clientInfo);
        // 记录 客户端信息 <-> 网络连接
        service.clientChannelCache().put(clientInfo, ctx.channel());

        OpentpMessage opentpMessageRes = OpentpCoreConstant.OPENTP_MSG_PROTO.clone();
        OpentpMessage
                .builder()
                .messageType(OpentpMessageTypeEnum.AUTHENTICATION_RES.getCode())
                .serializerType(SerializerTypeEnum.Kryo.getType())
                .data(new License(newLicenseKey))
                .traceId(opentpMessage.getTraceId())
                .buildTo(opentpMessageRes);

        // 返回 licenseKey
        ctx.channel().writeAndFlush(opentpMessageRes);
    }
}
