package com.zengshen.rabbit.api;

import com.zengshen.rabbit.api.exception.MessageRuntimeException;

import java.util.List;

public interface MessageProducer {
    void send(Message message, SendCallback sendCallback) throws MessageRuntimeException;

    void send(Message message) throws MessageRuntimeException;

    void send(List<Message> messageList) throws MessageRuntimeException;
}
