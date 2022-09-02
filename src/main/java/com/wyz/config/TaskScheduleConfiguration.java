package com.wyz.config;

import com.wyz.data.TaskDemo;
import com.wyz.registrar.CronRegistrar;
import com.wyz.repository.TaskRepository;
import com.wyz.task.TaskRunnable;
import com.wyz.util.CronUtil;
import com.wyz.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Configuration
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class TaskScheduleConfiguration implements ApplicationRunner {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CronRegistrar cronRegistrar;

    /**
     * 服务启动时扫描数据库并将今天的任务注册
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("服务启动，开始扫描数据库");
        OffsetDateTime now = OffsetDateTime.now();
        try {
            List<TaskDemo> taskList = taskRepository.findValidTask();
            for (TaskDemo task : taskList) {
                addTask(task, now);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 每天00:00:10扫描数据库，清空当前全部任务并添加今天的任务
     */
    @Async("taskExecutor")
    @Scheduled(cron = "10 0 0 * * ?")
    public void updateTask() {
        log.info("开始每日扫描数据库");
        cronRegistrar.removeAllCronTask();
        OffsetDateTime now = OffsetDateTime.now();
        List<TaskDemo> taskList = taskRepository.findValidTask();
        for (TaskDemo task : taskList) {
            boolean isValid = false;
            String[] crons = task.getCron().split(",");
            for (String cron : crons) {
                isValid |= CronUtil.isValidCron(cron, now, task.getEndTime());
            }
            if (!isValid) {
                taskRepository.setTaskInvalid(task.getId());
            }
            addTask(task, now);
        }
    }

    public void addTask(TaskDemo taskDemo, OffsetDateTime now) {
        String[] crons = taskDemo.getCron().split(",");
        String cron = crons[0];
        if (CronUtil.isInCron(cron, now, taskDemo.getSetTime(), taskDemo.getRepeatType(), taskDemo.getRepeatValue())) {
            log.info("添加任务");
            Runnable taskRunnable = new TaskRunnable(taskDemo);
            cronRegistrar.addCronTask(String.valueOf(taskDemo.getId()), taskRunnable, cron);
        }
        // 如果有提前提醒
        if (crons.length > 1) {
            String cron1 = crons[1];
            OffsetDateTime inAdvanceTime = DateUtil.getBeforeDateTime(taskDemo.getSetTime(), taskDemo.getInAdvanceType(), taskDemo.getInAdvanceValue());
            if (CronUtil.isInCron(cron1, now, inAdvanceTime, taskDemo.getRepeatType(), taskDemo.getRepeatValue())) {
                Runnable taskRunnable = new TaskRunnable(taskDemo);
                cronRegistrar.addCronTask("pre" + taskDemo.getId(), taskRunnable, cron);
            }
        }
    }
}
