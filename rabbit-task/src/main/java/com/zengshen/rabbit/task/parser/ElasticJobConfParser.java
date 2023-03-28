package com.zengshen.rabbit.task.parser;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.zengshen.rabbit.task.annotation.ElasticJobConfig;
import com.zengshen.rabbit.task.autoconfigure.JobZookeeperProperties;
import com.zengshen.rabbit.task.enums.ElasticJobTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author word
 */
@Slf4j
public class ElasticJobConfParser implements ApplicationListener<ApplicationReadyEvent> {


    private JobZookeeperProperties jobZookeeperProperties;
    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    public ElasticJobConfParser(JobZookeeperProperties jobZookeeperProperties, ZookeeperRegistryCenter zookeeperRegistryCenter) {
        this.jobZookeeperProperties = jobZookeeperProperties;
        this.zookeeperRegistryCenter = zookeeperRegistryCenter;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            ConfigurableApplicationContext applicationContext = event.getApplicationContext();
            Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(ElasticJobConfig.class);
            for (Object confBean : beanMap.values()) {
                Class<?> clazz = confBean.getClass();
                if (clazz.getName().indexOf("$") > 0) {
                    String className = clazz.getName();
                    clazz = Class.forName(className.substring(0, className.indexOf("$")));
                }
                // 获取接口类型， 用于判断是什么类型的任务
                Class<?>[] classes = clazz.getInterfaces();
                ElasticJobTypeEnum[] elasticJobTypeEnums = ElasticJobTypeEnum.values();
                List<Class<?>> jobTypeNameList = Arrays.stream(classes).filter(aClass -> {
                    String simpleName = aClass.getSimpleName();
                    boolean isTypeName = false;
                    for (ElasticJobTypeEnum elasticJobTypeEnum : elasticJobTypeEnums) {
                        if (simpleName.equals(elasticJobTypeEnum.getType())) {
                            isTypeName = true;
                            break;
                        }
                    }
                    return isTypeName;
                }).collect(Collectors.toList());
                if (jobTypeNameList.isEmpty()) {
                    log.info("没有指定定时任务的类型");
                    return;
                }
                String jobTypeName = jobTypeNameList.get(0).getSimpleName();
                //	获取配置项 ElasticJobConfig
                ElasticJobConfig conf = clazz.getAnnotation(ElasticJobConfig.class);
                String jobClass = clazz.getName();
                String jobName = this.jobZookeeperProperties.getNamespace() + "." + conf.name();
                String cron = conf.cron();
                String shardingItemParameters = conf.shardingItemParameters();
                String description = conf.description();
                String jobParameter = conf.jobParameter();
                String jobExceptionHandler = conf.jobExceptionHandler();
                String executorServiceHandler = conf.executorServiceHandler();

                String jobShardingStrategyClass = conf.jobShardingStrategyClass();
                String eventTraceRdbDataSource = conf.eventTraceRdbDataSource();
                String scriptCommandLine = conf.scriptCommandLine();

                boolean failover = conf.failover();
                boolean misfire = conf.misfire();
                boolean overwrite = conf.overwrite();
                boolean disabled = conf.disabled();
                boolean monitorExecution = conf.monitorExecution();
                boolean streamingProcess = conf.streamingProcess();

                int shardingTotalCount = conf.shardingTotalCount();
                int monitorPort = conf.monitorPort();
                int maxTimeDiffSeconds = conf.maxTimeDiffSeconds();
                int reconcileIntervalMinutes = conf.reconcileIntervalMinutes();

                // 当当网的configuration
                JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration.newBuilder(jobName, cron, shardingTotalCount)
                        .shardingItemParameters(shardingItemParameters)
                        .description(description)
                        .failover(failover)
                        .jobParameter(jobParameter)
                        .misfire(misfire)
                        .jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), jobExceptionHandler)
                        .jobProperties(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(), executorServiceHandler)
                        .build();
                JobTypeConfiguration jobTypeConfiguration = null;
                if (ElasticJobTypeEnum.SIMPLE.getType().equals(jobTypeName)) {
                    jobTypeConfiguration = new SimpleJobConfiguration(jobCoreConfiguration, jobClass);
                }
                if (ElasticJobTypeEnum.DATAFLOW.getType().equals(jobTypeName)) {
                    jobTypeConfiguration = new DataflowJobConfiguration(jobCoreConfiguration, jobClass, streamingProcess);
                }
                if (ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)) {
                    jobTypeConfiguration = new SimpleJobConfiguration(jobCoreConfiguration,
                            scriptCommandLine);
                }
                // LiteJobConfiguration
                LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(jobTypeConfiguration)
                        .overwrite(overwrite)
                        .disabled(disabled)
                        .monitorPort(monitorPort)
                        .monitorExecution(monitorExecution)
                        .maxTimeDiffSeconds(maxTimeDiffSeconds)
                        .jobShardingStrategyClass(jobShardingStrategyClass)
                        .reconcileIntervalMinutes(reconcileIntervalMinutes)
                        .build();
                // 创建spring的bean benDefinition
                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
                beanDefinitionBuilder.setInitMethodName("init");
                beanDefinitionBuilder.setScope("prototype");

                //	1.添加bean构造参数，相当于添加自己的真实的任务实现类
                if (!ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)) {
                    beanDefinitionBuilder.addConstructorArgValue(confBean);
                }
                //	2.添加注册中心
                beanDefinitionBuilder.addConstructorArgValue(this.zookeeperRegistryCenter);
                //	3.添加LiteJobConfiguration
                beanDefinitionBuilder.addConstructorArgValue(liteJobConfiguration);

                //	4.如果有eventTraceRdbDataSource 则也进行添加
                if (StringUtils.hasText(eventTraceRdbDataSource)) {
                    BeanDefinitionBuilder rdbFactory = BeanDefinitionBuilder.rootBeanDefinition(JobEventRdbConfiguration.class);
                    rdbFactory.addConstructorArgReference(eventTraceRdbDataSource);
                    beanDefinitionBuilder.addConstructorArgValue(rdbFactory.getBeanDefinition());
                }

                //  5.添加监听
                List<?> elasticJobListeners = getTargetElasticJobListeners(conf);
                beanDefinitionBuilder.addConstructorArgValue(elasticJobListeners);

                // 	接下来就是把beanDefinitionBuilder 也就是 SpringJobScheduler注入到Spring容器中
                // 能自动注入
                DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

                String registerBeanName = conf.name() + "SpringJobScheduler";
                // 注入
                defaultListableBeanFactory.registerBeanDefinition(registerBeanName, beanDefinitionBuilder.getBeanDefinition());
                SpringJobScheduler scheduler = (SpringJobScheduler) applicationContext.getBean(registerBeanName);
                scheduler.init();
                log.info("启动elastic-job作业: " + jobName);
            }
            log.info("启动作业数: {}", beanMap.values().size());
        } catch (ClassNotFoundException e) {
            log.error("elasticJob 启动异常, 系统强制退出 ", e);
            System.exit(1);
        }
    }

    private List<BeanDefinition> getTargetElasticJobListeners(ElasticJobConfig conf) {
        List<BeanDefinition> result = new ManagedList<BeanDefinition>(2);
        String listeners = conf.listener();
        if (StringUtils.hasText(listeners)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listeners);
            factory.setScope("prototype");
            result.add(factory.getBeanDefinition());
        }

        String distributedListeners = conf.distributedListener();
        long startedTimeoutMilliseconds = conf.startedTimeoutMilliseconds();
        long completedTimeoutMilliseconds = conf.completedTimeoutMilliseconds();

        if (StringUtils.hasText(distributedListeners)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListeners);
            factory.setScope("prototype");
            factory.addConstructorArgValue(startedTimeoutMilliseconds);
            factory.addConstructorArgValue(completedTimeoutMilliseconds);
            result.add(factory.getBeanDefinition());
        }
        return result;
    }
}


