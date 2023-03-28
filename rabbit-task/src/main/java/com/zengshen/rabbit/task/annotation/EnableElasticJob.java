package com.zengshen.rabbit.task.annotation;

import com.zengshen.rabbit.task.autoconfigure.JobParserAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author word
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(JobParserAutoConfiguration.class)
public @interface EnableElasticJob {

}

