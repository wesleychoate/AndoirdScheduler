package com.wac.android.finalscheduler.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;
import com.wac.android.finalscheduler.MainActivity;

import java.nio.channels.AlreadyBoundException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class NotificationScheduler {
    final private WorkManager workManager;
    final private Context context;

    public NotificationScheduler(Context context) {
        workManager = WorkManager.getInstance(context);
        this.context = context;
    }

    public void cancelNotification(UUID jobId) {
        workManager.cancelWorkById(jobId);
    }

    public WorkInfo.State getNotificationState(UUID jobId) {
        ListenableFuture<WorkInfo> job = workManager.getWorkInfoById(jobId);
        try {
            WorkInfo jobInfo = job.get();
            return jobInfo.getState();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public UUID scheduleNotification(LocalDate notifyOn, String title, String message) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime notificationDate = notificationDate = notifyOn.atTime(8, 0);
        //ZonedDateTime notificationDate = notifyOn.atTime(8, 0).atZone(ZoneId.systemDefault());
        //LocalDateTime notificationDate = now.plusSeconds(120);
        Duration timeBetween = Duration.between(now, notificationDate);

         Data notificationData = new Data.Builder()
                .putString("title", title)
                .putString("message", message)
                .build();

        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationHelper.class)
                .setInitialDelay(timeBetween)
                .setInputData(notificationData)
                .build();

        workManager.enqueue(notificationWork);
        return notificationWork.getId();

        /*ComponentName rcvr = new ComponentName(context, MainActivity.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(rcvr,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("title", title);
        i.putExtra("message", message);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1, i, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationDate.toInstant().toEpochMilli(), pi);

        return null; */
    }
}
