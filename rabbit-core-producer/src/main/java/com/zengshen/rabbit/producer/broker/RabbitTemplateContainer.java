package com.zengshen.rabbit.producer.broker;

import com.google.common.base.Splitter;
import com.zengshen.rabbit.api.Message;
import com.zengshen.rabbit.api.MessageType;
import com.zengshen.rabbit.api.exception.MessageRuntimeException;
import com.zengshen.rabbit.common.convert.GenericMessageConverter;
import com.zengshen.rabbit.common.convert.RabbitMessageConverter;
import com.zengshen.rabbit.common.serializer.Serializer;
import com.zengshen.rabbit.common.serializer.SerializerFactory;
import com.zengshen.rabbit.common.serializer.impl.JacksonSerializerFactory;
import com.zengshen.rabbit.producer.service.MessageStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class RabbitTemplateContainer  implements RabbitTemplate.ConfirmCallback {

    private final Map<String, RabbitTemplate> rabbitMap = new HashMap<>();
    private static Splitter splitter = Splitter.on("#");

    SerializerFactory serializerFactory = JacksonSerializerFactory.INSTANCE;

    @Autowired
    private MessageStoreService messageStoreService;

    public RabbitTemplate getTemplate(Message message) {
        if (message == null) {
            throw new MessageRuntimeException("消息不能为空");
        }
        String topic = message.getTopic();
        RabbitTemplate rabbitTemplate = rabbitMap.get(topic);
        if (rabbitTemplate != null) {
            return rabbitTemplate;
        }
        log.info("#RabbitTemplateContainer.getTemplate# 主题： {} 是空的", topic);
        RabbitTemplate newRabbitTemplate = new RabbitTemplate();
        newRabbitTemplate.setExchange(topic);
        newRabbitTemplate.setRetryTemplate(new RetryTemplate());
        newRabbitTemplate.setRoutingKey(message.getRoutingKey());
        // 对于message的序列化
        // 转为他的，转为我的

        Serializer serializer = serializerFactory.create();
        GenericMessageConverter gmc = new GenericMessageConverter(serializer);
        RabbitMessageConverter rmc = new RabbitMessageConverter(gmc);
        newRabbitTemplate.setMessageConverter(rmc);

        String messageType = message.getMessageType();
        if (!MessageType.RAPID.equals(messageType)) {
             newRabbitTemplate.setConfirmCallback(this);
        }
        rabbitMap.putIfAbsent(topic, newRabbitTemplate);
        return rabbitMap.get(topic);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        // 消息的应答
        List<String> splitToList = splitter.splitToList(correlationData.getId());
        String messageId = splitToList.get(0);
        Long sendTime = Long.valueOf(splitToList.get(1));
        if (ack) {
            // 当broker返回ack成功时, 把数据库的消息设置状态为ok
            messageStoreService.success(messageId);
            log.info("发送消息成功, confirm messageId: {}, 发送时间: {}", messageId, sendTime);
        } else{
            log.error("发送消息失败, confirm messageId: {}, 发送时间: {}", messageId, sendTime);
        }
    }
}
