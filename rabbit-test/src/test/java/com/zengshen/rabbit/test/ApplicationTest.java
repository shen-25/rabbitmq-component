package com.zengshen.rabbit.test;

import com.google.common.collect.Maps;
import com.zengshen.rabbit.api.Message;
import com.zengshen.rabbit.api.MessageType;
import com.zengshen.rabbit.producer.broker.ProducerClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    @Autowired
    private ProducerClient producerClient;

    /**
     * 迅速消息测试
     * @throws Exception
     */
    @Test
    public void testProducerClient() throws Exception {
            String uniqueId = UUID.randomUUID().toString();
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("name", "曾深");
            attributes.put("age", "18");
            attributes.put("class", "东莞理工学院");
            Message message = new Message(
                    uniqueId,
                    "exchange-learn1",
                    "springboot.abc",
                    attributes,
                    0);
            message.setMessageType(MessageType.RELIANT);
            producerClient.send(message);

        Thread.sleep(1000000);
    }

    /**
     * 延迟消息发送测试
     * @throws Exception
     */
    @Test
    public void delayTest() throws Exception {

        String uniqueId = UUID.randomUUID().toString();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "曾深");
        attributes.put("age", "88");
        Message message = new Message(
                uniqueId,
                "delay-message",
                "delay.zengshen",
                attributes,
                15000);
        message.setMessageType(MessageType.RELIANT);
        producerClient.send(message);

        Thread.sleep(10000);
    }

    /**
     * 批量消息测试
     * @throws Exception
     */
    @Test
    public void setMessageList() throws Exception {
        String uniqueId = UUID.randomUUID().toString();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "曾深");
        attributes.put("age", "18");
        attributes.put("class", "东莞理工学院");
        Message message = new Message(
                uniqueId,
                "exchange-learn",
                "springboot.abc",
                attributes,
                0);
        // 复制一份
        HashMap<String, Object> hashMap = Maps.newHashMap(attributes);
        hashMap.put("tips", "第二个消息");
        Message message2 = new Message(
                uniqueId,
                "exchange-learn",
                "springboot.abc",
                hashMap,
                0);
        List<Message> messageList = new ArrayList<>();
        messageList.add(message);
        messageList.add(message2);
        producerClient.send(messageList);

        Thread.sleep(1000);
    }

}