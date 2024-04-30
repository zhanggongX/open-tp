package cn.opentp.core.constant;

import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageConstant;
import io.netty.util.AttributeKey;

public class OpentpCoreConstant {

    public static final String EXPORT_CHANNEL_LICENSE_KEY = "licenseKey";
    // licenseKey
    public static final AttributeKey<String> EXPORT_CHANNEL_ATTR_KEY = AttributeKey.valueOf(EXPORT_CHANNEL_LICENSE_KEY);

    // opentp 消息原型
    public static final OpentpMessage OPENTP_MSG_PROTO = new OpentpMessage(OpentpMessageConstant.MAGIC, OpentpMessageConstant.VERSION);
}
