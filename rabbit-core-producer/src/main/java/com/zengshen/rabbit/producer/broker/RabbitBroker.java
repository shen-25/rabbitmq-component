package com.zengshen.rabbit.producer.broker;

import com.zengshen.rabbit.api.Message;

/**
 * 具体发送不同种类类型消息接口
 */
public interface RabbitBroker {

    void rapidSend(Message message);

    void confirmSend(Message message);

    void reliantSend(Message message);

    void sendMessages();
}
