package com.zengshen.rabbit.producer.entity.constant;

/**
 * 消息的发送状态
 * @author word
 */
public enum BrokerMessageStatus {
    /**
     *
     */
    SENDING("0"),
    SEND_OK("1"),
    SEND_FAIL("2");

    private String code;

    private BrokerMessageStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
