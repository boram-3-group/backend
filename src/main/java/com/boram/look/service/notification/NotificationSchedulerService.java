package com.boram.look.service.notification;

import com.boram.look.domain.notification.NotificationDayOfWeek;
import com.boram.look.domain.notification.UserNotificationSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;

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

        String cron = toCronExpression(setting.getHour(), setting.getMinute(), setting.getDayOfWeek().toString());

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, "user-alarms")
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();

        try {
            JobKey jobKey = new JobKey(jobName, "user-alarms");
            TriggerKey triggerKey = new TriggerKey(triggerName, "user-alarms");

            if (scheduler.checkExists(triggerKey)) {
                scheduler.unscheduleJob(triggerKey); // 기존 트리거 제거
            }

            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey); // 기존 잡 제거
            }

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("scheduleUserNotification exception occur.");
            e.printStackTrace();
        }
    }

    public void deleteUserNotification(Long settingId) {
        JobKey jobKey = new JobKey("user_alarm_" + settingId, "user-alarms");
        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }
    }

    private String toCronExpression(int hour, int minute, String daysOfWeekCsv) {
        // 예: "MON,WED,FRI" → "MON,WED,FRI"
        String dayOfWeek = daysOfWeekCsv;
        if (daysOfWeekCsv.equals(NotificationDayOfWeek.EVERYDAY.toString())) {
            dayOfWeek = "*";
        }
        return String.format("0 %d %d ? * %s", minute, hour, dayOfWeek);
    }
}
