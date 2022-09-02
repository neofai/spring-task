package com.wyz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ThreadPoolConfiguration {
    private static final Integer TASK_POOL_SIZE = 10;
    private static final String TASK_POOL_GROUP_NAME = "springTask";
    private static final String TASK_POOL_NAME_PREFIX = "taskThread-";


    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(TASK_POOL_SIZE);
        threadPoolTaskScheduler.setThreadGroupName(TASK_POOL_GROUP_NAME);
        threadPoolTaskScheduler.setThreadNamePrefix(TASK_POOL_NAME_PREFIX);
        threadPoolTaskScheduler.setDaemon(true);
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }
}
