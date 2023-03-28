package com.zengshen.rabbit.producer.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.zengshen.rabbit.producer.broker.RabbitBroker;
import com.zengshen.rabbit.producer.entity.BrokerMessage;
import com.zengshen.rabbit.producer.entity.constant.BrokerMessageStatus;
import com.zengshen.rabbit.producer.service.MessageStoreService;
import com.zengshen.rabbit.task.annotation.ElasticJobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author word
 */
@Component
@Slf4j
@ElasticJobConfig(
        name = "com.zengshen.rabbit.task.RetryMessageDataflowJob",
        cron = "0/5 * * * * ?",
        description = "可靠性消息补偿任务",
        overwrite = true,
        shardingTotalCount = 1
)
public class RetryMessageDataflowJob implements DataflowJob<BrokerMessage> {

    @Autowired
    private MessageStoreService messageStoreService;

    @Autowired
    private RabbitBroker rabbitBroker;

    private final static int MAX_RETRY_COUNT = 3;

    @Override
    public List<BrokerMessage> fetchData(ShardingContext shardingContext) {
        List<BrokerMessage> brokerMessages = messageStoreService.fetchTimeOutMessage4Retry(BrokerMessageStatus.SENDING);
        log.info("---------抓取的数据数量, 数量: {} ------", brokerMessages.size());
        return brokerMessages;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<BrokerMessage> dataList) {
        dataList.forEach(brokerMessage -> {
            String messageId = brokerMessage.getMessageId();
            if (brokerMessage.getTryCount() >= MAX_RETRY_COUNT) {
               messageStoreService.failure(messageId);
               log.warn("消息重试失败，消息设置为最终失败， 消息的id: {}", messageId);
            }else{
                messageStoreService.updateTryCount(messageId);
                this.rabbitBroker.rapidSend(brokerMessage.getMessage());
            }
        });

    }
}
