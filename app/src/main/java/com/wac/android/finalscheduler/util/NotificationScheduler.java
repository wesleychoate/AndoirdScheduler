package com.wac.android.finalscheduler.util;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public UUID scheduleNotification(LocalDate notifyOn, String title, String message) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime notificationDate = notifyOn.atTime(8, 0);
        //LocalDateTime notificationDate = now.plusSeconds(120);
        Duration timeBetween = Duration.between(now, notificationDate);

        Data notificationData = new Data.Builder()
                .putString("title", title)
                .putString("message", message)
                .build();

        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationHelper.class)
                .setInitialDelay(timeBetween.getSeconds() * 1000L, TimeUnit.MILLISECONDS)
                .setInputData(notificationData)
                .build();

        workManager.enqueue(notificationWork);
        return notificationWork.getId();
    }
}
