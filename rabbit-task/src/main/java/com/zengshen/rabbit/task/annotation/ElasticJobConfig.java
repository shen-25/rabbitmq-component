package com.zengshen.rabbit.task.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author word
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticJobConfig {
    /**
     * elasticJob 名称
     */
    String name();

    /**
     * cron表达式，用于控制作业触发时间
     *
     */
    String cron() default "";

    /**
     * 作业分片总数
     * @return int
     */
    int shardingTotalCount() default  1;

    /**
     * 分片序列号和参数用等号分隔，多个键值对用逗号分隔
     * 分片序列号从0开始，不可大于或等于作业分片总数
     * 如：
     * 0=a,1=b,2=c
     * @return String
     */
    String shardingItemParameters() default "";

    /**
     * 作业自定义参数
     * 作业自定义参数，可通过传递该参数为作业调度的业务方法传参，用于实现带参数的作业
     * 例：每次获取的数据量、作业实例从数据库读取的主键等
     */
    String jobParameter() default "";

    /**
     * 是否开启任务执行失效转移，开启表示如果作业在一次任务执行中途宕机，允许将该次未完成的任务在另一作业节点上补偿执行
     */
    boolean failover() default false;

    boolean misfire() default true;

    String description() default "";

    boolean overwrite() default false;

    boolean streamingProcess() default false;

    String scriptCommandLine() default "";

    boolean monitorExecution() default false;

    int monitorPort() default -1;	//must

    int maxTimeDiffSeconds() default -1;	//must

    String jobShardingStrategyClass() default "";	//must

    int reconcileIntervalMinutes() default 10;	//must

    String eventTraceRdbDataSource() default "";	//must

    String listener() default "";	//must

    boolean disabled() default false;	//must

    String distributedListener() default "";

    long startedTimeoutMilliseconds() default Long.MAX_VALUE;	//must

    long completedTimeoutMilliseconds() default Long.MAX_VALUE;		//must

    String jobExceptionHandler() default "com.dangdang.ddframe.job.executor.handler.impl.DefaultJobExceptionHandler";

    public String executorServiceHandler() default "com.dangdang.ddframe.job.executor.handler.impl.DefaultExecutorServiceHandler";
}

