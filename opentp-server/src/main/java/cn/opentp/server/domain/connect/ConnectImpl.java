package cn.opentp.server.domain.connect;

import cn.opentp.core.auth.License;
import cn.opentp.core.constant.OpentpCoreConstant;
import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.core.net.serializer.SerializerTypeEnum;
import cn.opentp.server.domain.EventQueue;
import cn.opentp.server.infrastructure.auth.LicenseFactory;
import cn.opentp.server.infrastructure.auth.LicenseKeyFactory;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class ConnectImpl implements Connect {

    @Inject
    private LicenseFactory licenseFactory;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String host;
    private String pid;
    private String appKey;
    private String appSecret;

    public ConnectImpl() {
    }

    public ConnectImpl(String host, String pid, String appKey, String appSecret) {
        this.host = host;
        this.pid = pid;
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public void handle(EventQueue eventQueue, ConnectCommand command) {

        log.debug("new connect host: {}, instance: {}, appKey: {}, appSecret: {}", command.getHost(), command.getPid(), command.getAppKey(), "...");
        if (command.getAppKey() == null) {
            log.warn("new connect，unknown appKey : {}", command.getAppKey());
            throw new UnsupportedOperationException("unknown appKey");
        }

        String newLicenseKey = licenseFactory.get();
        log.debug("new connect auth success, new licenseKey : {}", newLicenseKey);

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
