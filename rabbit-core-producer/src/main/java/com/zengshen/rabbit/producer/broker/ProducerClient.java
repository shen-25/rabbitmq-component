package com.zengshen.rabbit.producer.broker;

import com.zengshen.rabbit.api.Message;
import com.zengshen.rabbit.api.MessageProducer;
import com.zengshen.rabbit.api.SendCallback;
import com.zengshen.rabbit.api.exception.MessageRuntimeException;

import java.util.List;

public class ProducerClient implements MessageProducer {

    @Override
    public void send(Message message, SendCallback sendCallback) throws MessageRuntimeException {

    }

    @Override
    public void send(Message message) throws MessageRuntimeException {

    }

    @Override
    public void send(List<Message> messageList) throws MessageRuntimeException {

    }
}
