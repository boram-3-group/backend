package com.boram.look.service.notification;

import com.boram.look.domain.notification.UserNotificationSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSchedulerService {

    private final Scheduler scheduler;

    @Transactional(readOnly = true)
    public void scheduleUserNotification(UserNotificationSetting setting) {
        String jobName = "user_alarm_" + setting.getId();
        String triggerName = "trigger_" + setting.getId();

        JobDetail jobDetail = JobBuilder.newJob(SendNotificationJob.class)
                .withIdentity(jobName, "user-alarms")
                .usingJobData("userSettingId", setting.getId())
                .build();

        String cron = toCronExpression(setting.getHour(), setting.getMinute(), setting.getDayOfWeek().toString()); // 아래 설명

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, "user-alarms")
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            log.error("scheduleUserNotification exception occur.");
            e.printStackTrace();
        }
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
