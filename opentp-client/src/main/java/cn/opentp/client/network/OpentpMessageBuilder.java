package cn.opentp.client.network;

import cn.opentp.core.auth.ClientInfo;
import cn.opentp.core.constant.OpentpCoreConstant;
import cn.opentp.core.net.OpentpMessage;
import cn.opentp.core.net.OpentpMessageTypeEnum;
import cn.opentp.core.net.serializer.SerializerTypeEnum;
import cn.opentp.core.util.MessageTraceIdUtil;

import java.util.function.Function;

public class OpentpMessageBuilder {

    public static Function<ClientInfo, OpentpMessage> authMessage = clientInfo -> {
        OpentpMessage opentpMessage = OpentpCoreConstant.OPENTP_MSG_PROTO.clone();
        OpentpMessage
                .builder()
                .messageType(OpentpMessageTypeEnum.AUTHENTICATION_REQ.getCode())
                .serializerType(SerializerTypeEnum.Kryo.getType())
                .traceId(MessageTraceIdUtil.traceId())
                .data(clientInfo)
                .buildTo(opentpMessage);

        return opentpMessage;
    };
}
