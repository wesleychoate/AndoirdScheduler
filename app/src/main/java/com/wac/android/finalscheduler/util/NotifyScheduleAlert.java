package com.wac.android.finalscheduler.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.wac.android.finalscheduler.R;

import java.util.SplittableRandom;

public class NotifyScheduleAlert  {

    private static final String CHANNEL_ID = "1111";
    private static final String GROUP_KEY_SCHEDULE = "com.wac.android.finalscheduler.GROUP";

    public static void showNotification(Context context, Class<?> cls, String title, String content) {
        Intent intent = new Intent(context, String.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.schedule_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setGroup(GROUP_KEY_SCHEDULE)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        int id = new SplittableRandom().nextInt(1, 9999999);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, builder.build());
    }
}
