package com.zengshen.rabbit.common.convert;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.lang.reflect.Type;

/**
 * 装饰者模式
 * @author word
 */
public class RabbitMessageConverter implements MessageConverter {
    private GenericMessageConverter delegate;
    private final String defaultExpire = String.valueOf(24 * 60 * 60 * 1000);

    public RabbitMessageConverter(GenericMessageConverter genericMessageConverter) {
        if (genericMessageConverter == null) {
            throw new RuntimeException("genericMessageConverter不能为空");
        }
        this.delegate = genericMessageConverter;
    }

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        messageProperties.setExpiration(defaultExpire);
//        messageProperties.setContentEncoding("utf-8");
        return this.delegate.toMessage(object, messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        return (com.zengshen.rabbit.api.Message)this.delegate.fromMessage(message);
    }
}
