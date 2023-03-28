package com.zengshen.rabbit.producer.broker;

import com.zengshen.rabbit.api.Message;
import com.zengshen.rabbit.api.MessageType;
import com.zengshen.rabbit.producer.entity.BrokerMessage;
import com.zengshen.rabbit.producer.entity.constant.BrokerMessageConst;
import com.zengshen.rabbit.producer.entity.constant.BrokerMessageStatus;
import com.zengshen.rabbit.producer.service.MessageStoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * @author word
 */
@Component
@Slf4j
public class RabbitBrokerImpl implements RabbitBroker {

    @Autowired
    private RabbitTemplateContainer rabbitTemplateContainer;

    @Autowired
    private MessageStoreService messageStoreService;

    @Override
    public void reliantSend(Message message) {
        BrokerMessage bm = messageStoreService.selectByMessageId(message.getMessageId());
        if (bm == null) {
            // 发送的消息进数据库
            BrokerMessage brokerMessage = new BrokerMessage();
            brokerMessage.setMessageId(message.getMessageId());
            brokerMessage.setStatus(BrokerMessageStatus.SENDING.getCode());
            // tryCount 第一次不用设置
            Date now = new Date();
            brokerMessage.setNextRetry(DateUtils.addMinutes(now, BrokerMessageConst.TIMEOUT));
            brokerMessage.setCreateTime(now);
            brokerMessage.setUpdateTime(now);
            brokerMessage.setMessage(message);
            messageStoreService.insert(brokerMessage);
        }
        // 发送消息
        sendKernel(message);
    }

    @Override
    public void confirmSend(Message message) {
        message.setMessageType(MessageType.CONFIRM);
        this.sendKernel(message);
    }

    @Override
    public void rapidSend(Message message) {
        message.setMessageType(MessageType.RAPID);
        sendKernel(message);
    }
    
    @Override
    public void sendMessages() {

    }

    /**
     * 发送消息的核心方法
     * @param message
     */
    private void sendKernel(Message message) {
        AsyncBaseQueue.submit(new Runnable() {
            @Override
            public void run() {
                CorrelationData correlationData = new CorrelationData(String.format("%s#%s#%s",
                        message.getMessageId(), System.currentTimeMillis(), message.getMessageType()));
                String routingKey = message.getRoutingKey();
                String topic = message.getTopic();
                RabbitTemplate rabbitTemplate = rabbitTemplateContainer.getTemplate(message);
                rabbitTemplate.convertAndSend(topic, routingKey,
                        message, correlationData);

                log.info("# RabbitBrokerImpl.sendKernel#  发送消息，消息的id: {}", message.getMessageId());
            }
        });

    }
}
