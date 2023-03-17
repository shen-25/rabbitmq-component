package com.zengshen.rabbit.api;

/**
 * 消费者监听
 */
public interface MessageListener {

    void onMessage(Message message);
}
