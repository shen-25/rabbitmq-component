package com.zengshen.rabbit.producer.broker;

import com.zengshen.rabbit.api.Message;
import com.zengshen.rabbit.api.MessageProducer;
import com.zengshen.rabbit.api.MessageType;
import com.zengshen.rabbit.api.SendCallback;
import com.zengshen.rabbit.api.exception.MessageRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author word
 * 发送消息的实现类
 */
@Component
public class ProducerClient implements MessageProducer {

    @Autowired
    private RabbitBroker rabbitBroker;

    @Override
    public void send(Message message) throws MessageRuntimeException {
        if (message.getTopic() == null) {
            throw new MessageRuntimeException("消息的主题不能为空");
        }
        String messageType = message.getMessageType();
        switch (messageType) {
            case MessageType.RAPID:
                rabbitBroker.rapidSend(message);
                break;
            case MessageType.CONFIRM:
                rabbitBroker.confirmSend(message);
                break;
            case MessageType.RELIANT:
                rabbitBroker.reliantSend(message);
                break;
            default:
                break;
        }
    }

    /**
     * 认为发送批量消息的类型都为迅速消息
     * @param messageList
     * @throws MessageRuntimeException
     */
    @Override
    public void send(List<Message> messageList) throws MessageRuntimeException {
        messageList.forEach(message -> {
            message.setMessageType(MessageType.RAPID);
            MessageHolder.add(message);
        });
        rabbitBroker.sendMessages();
    }

    @Override
    public void send(Message message, SendCallback sendCallback) throws MessageRuntimeException {

    }
}
