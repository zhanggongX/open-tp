package cn.opentp.gossip.enums;

public enum MessageTypeEnum {
    SYNC,
    ACK,
    ACK2,
    SHUTDOWN,
    GOSSIP;

    public static MessageTypeEnum findByType(String type) {
        for (MessageTypeEnum messageTypeEnum : MessageTypeEnum.values()) {
            if (messageTypeEnum.name().equals(type)) {
                return messageTypeEnum;
            }
        }
        return null;
    }
}
