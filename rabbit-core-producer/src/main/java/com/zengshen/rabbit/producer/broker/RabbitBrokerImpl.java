package com.zengshen.rabbit.producer.broker;

import com.zengshen.rabbit.api.Message;
import com.zengshen.rabbit.api.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author word
 */
@Component
@Slf4j
public class RabbitBrokerImpl implements RabbitBroker {

    @Autowired
    private RabbitTemplateContainer rabbitTemplateContainer;

    @Override
    public void rapidSend(Message message) {
        message.setMessageType(MessageType.RAPID);
        sendKernel(message);
    }

    /**
     * 发送消息的核心方法
     * @param message
     */
    private void sendKernel(Message message) {
        AsyncBaseQueue.submit(new Runnable() {
            @Override
            public void run() {
                CorrelationData correlationData = new CorrelationData(String.format("%s#%s",
                        message.getMessageId(), System.currentTimeMillis()));

                String routingKey = message.getRoutingKey();
                String topic = message.getTopic();
                RabbitTemplate rabbitTemplate = rabbitTemplateContainer.getTemplate(message);
                rabbitTemplate.convertAndSend(topic, routingKey,
                        message, correlationData);
                log.info("# RabbitBrokerImpl.sendKernel#  发送消息，消息的id: {}", message.getMessageId());
            }
        });

    }

    @Override
    public void confirmSend(Message message) {

    }

    @Override
    public void reliantSend(Message message) {

    }

    @Override
    public void sendMessages() {

    }
}
