package cn.opentp.gossip.enums;

public enum MessageTypeEnum {

    SYNC_MESSAGE("sync_message"),
    ACK_MESSAGE("ack_message"),
    ACK2_MESSAGE("ack2_message"),
    SHUTDOWN("shutdown"),
    REG_MESSAGE("reg_message");

    private final String type;

    MessageTypeEnum(String type) {
        this.type = type;
    }

    public static MessageTypeEnum findByType(String type) {
        for (MessageTypeEnum messageTypeEnum : MessageTypeEnum.values()) {
            if (messageTypeEnum.getType().equals(type)) {
                return messageTypeEnum;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }
}
