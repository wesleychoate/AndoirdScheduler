package com.wac.android.finalscheduler.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wac.android.finalscheduler.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {

    String SCHEDULER_TAG = "NotificationAlertReceived";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotifyScheduleAlert.showNotification(context, MainActivity.class,
                intent.getStringExtra("title"),
                intent.getStringExtra("message"));
    }
}
