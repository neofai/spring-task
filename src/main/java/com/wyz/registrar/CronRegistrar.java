package com.wyz.registrar;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CronRegistrar implements DisposableBean {
    private final Map<String, ScheduledTask> scheduledTaskMap = new ConcurrentHashMap<>();

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    public void addCronTask(String id, Runnable task, String cronExpression) {
        addCronTask(id, new CronTask(task, cronExpression));
    }

    public void addCronTask(String id, CronTask cronTask) {
        if (cronTask != null) {
            if (this.scheduledTaskMap.containsKey(id)) {
                removeCronTask(id);
            }
            this.scheduledTaskMap.put(id, scheduledCronTask(cronTask));
        }
    }

    public void removeCronTask(String id) {
        ScheduledTask scheduledTask = this.scheduledTaskMap.remove(id);
        if (scheduledTask != null) {
            scheduledTask.cancel();
        }
    }

    public void removeAllCronTask() {
        for (String id : this.scheduledTaskMap.keySet()) {
            removeCronTask(id);
        }
    }

    public ScheduledTask scheduledCronTask(CronTask cronTask) {
        ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();
        registrar.setTaskScheduler(taskScheduler);
        return registrar.scheduleCronTask(cronTask);
    }

    @Override
    public void destroy() {
        for (ScheduledTask task : this.scheduledTaskMap.values()) {
            task.cancel();
        }
        this.scheduledTaskMap.clear();
    }


}
