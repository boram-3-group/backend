package com.boram.look.service.notification;

import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

    private final Scheduler scheduler;

    public void scheduleUserNotification(UUID userId, int hour, int minute, String daysOfWeek) throws SchedulerException {
        String jobName = "user_alarm_" + userId;
        String triggerName = "trigger_" + userId;

        JobDetail jobDetail = JobBuilder.newJob(SendNotificationJob.class)
                .withIdentity(jobName, "user-alarms")
                .usingJobData("userId", userId.toString())
                .build();

        String cron = toCronExpression(hour, minute, daysOfWeek); // 아래 설명

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, "user-alarms")
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void deleteUserNotification(Long userId) throws SchedulerException {
        JobKey jobKey = new JobKey("user_alarm_" + userId, "user-alarms");
        scheduler.deleteJob(jobKey);
    }

    private String toCronExpression(int hour, int minute, String daysOfWeekCsv) {
        // 예: "MON,WED,FRI" → "MON,WED,FRI"
        return String.format("0 %d %d ? * %s", minute, hour, daysOfWeekCsv);
    }
}
