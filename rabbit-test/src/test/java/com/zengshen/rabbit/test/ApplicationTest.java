package com.zengshen.rabbit.test;

import com.zengshen.rabbit.api.Message;
import com.zengshen.rabbit.api.MessageType;
import com.zengshen.rabbit.producer.broker.ProducerClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    @Autowired
    private ProducerClient producerClient;

    @Test
    public void testProducerClient() throws Exception {

            String uniqueId = UUID.randomUUID().toString();
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("name", "张三");
            attributes.put("age", "18");
            Message message = new Message(
                    uniqueId,
                    "exchange-learn",
                    "springboot.abc",
                    attributes,
                    0);
            message.setMessageType(MessageType.RELIANT);
            producerClient.send(message);

        Thread.sleep(1000);
    }

    @Test
    public void delayTest() throws Exception {

        String uniqueId = UUID.randomUUID().toString();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "曾深");
        attributes.put("age", "18");
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

}