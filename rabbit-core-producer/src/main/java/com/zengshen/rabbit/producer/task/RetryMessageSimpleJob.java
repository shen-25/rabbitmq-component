package com.zengshen.rabbit.producer.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.zengshen.rabbit.producer.broker.RabbitBroker;
import com.zengshen.rabbit.producer.entity.BrokerMessage;
import com.zengshen.rabbit.producer.entity.constant.BrokerMessageStatus;
import com.zengshen.rabbit.producer.service.MessageStoreService;
import com.zengshen.rabbit.task.annotation.ElasticJobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Slf4j
@ElasticJobConfig(
        name = "simpleJobTest", cron = "0/10 * * * * ?",
        description = "简单任务测试"
)
public class RetryMessageSimpleJob implements Comparable<Object>, SimpleJob, Serializable {

    @Autowired
    private MessageStoreService messageStoreService;

    @Autowired
    private RabbitBroker rabbitBroker;
    private final static int MAX_RETRY_COUNT = 3;

    public void execute(ShardingContext shardingContext) {
        List<BrokerMessage> dataList = messageStoreService.fetchTimeOutMessage4Retry(BrokerMessageStatus.SENDING);
        dataList.forEach(brokerMessage -> {
            String messageId = brokerMessage.getMessageId();
            if (brokerMessage.getTryCount() >= MAX_RETRY_COUNT) {
                messageStoreService.failure(messageId);
                log.warn("消息重试失败，消息设置为最终失败， 消息的id: {}", messageId);
            }else{
                messageStoreService.updateTryCount(messageId);
                log.warn("消息的补偿机制已开启，正在尝试重新发送消息....");
                this.rabbitBroker.rapidSend(brokerMessage.getMessage());
            }
        });
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
