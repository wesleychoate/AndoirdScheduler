package com.wac.android.finalscheduler.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.wac.android.finalscheduler.R;

import java.util.SplittableRandom;

public class NotificationHelper extends Worker {

    private static final String CHANNEL_ID = "1111";
    String GROUP_KEY_SCHEDULE = "com.wac.android.finalscheduler.GROUP";

    public NotificationHelper(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = applicationContext.getString(R.string.channel_name);
            String description = applicationContext.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = applicationContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(getApplicationContext(), String.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.schedule_launcher_foreground)
                .setContentTitle(getInputData().getString("title"))
                .setContentText(getInputData().getString("message"))
                .setGroup(GROUP_KEY_SCHEDULE)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        int id = new SplittableRandom().nextInt(1, 9999999);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(id, builder.build());

        return Result.success();
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }
}