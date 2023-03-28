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

        for(int i = 0 ; i < 1; i ++) {
            String uniqueId = UUID.randomUUID().toString();
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("name", "张三");
            attributes.put("age", "18");
            Message message = new Message(
                    uniqueId,
                    "exchange-learnfddfsdfs",
                    "springboot.abc",
                    attributes,
                    0);
            message.setMessageType(MessageType.RELIANT);
//			message.setDelayMills(15000);
            producerClient.send(message);
        }

        Thread.sleep(1000000);
    }

}