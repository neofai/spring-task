package com.wyz.task;

import com.wyz.data.TaskDemo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

/**
 * run方法中写具体的定时任务, 通常会将TaskDemo作为参数传入构造方法中，来用到TaskDemo中的某些参数，如title，content等
 */
@Component
@Scope(value = "prototype")
@Slf4j
public class TaskRunnable implements Runnable {

    private TaskDemo taskDemo;

    public TaskRunnable() {

    }

    public TaskRunnable(TaskDemo taskDemo) {
        this.taskDemo = taskDemo;
    }

    @Override
    public void run() {
        log.info("开始执行定时任务,time:{}", OffsetDateTime.now());
        // task code

    }
}
