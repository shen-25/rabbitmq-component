package com.zengshen.rabbit.producer.autoconfiguration;

import com.zengshen.rabbit.task.annotation.EnableElasticJob;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author word
 * 自动装配的类
 */
@EnableElasticJob
@Configuration
@ComponentScan({"com.zengshen.rabbit.producer"})
public class RabbitProducerAutoConfiguration {

}
