package com.bosictsolution.invsale.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.bosictsolution.invsale.R;
import com.bosictsolution.invsale.SplashActivity;
import com.bosictsolution.invsale.common.AppConstant;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {
    public static void displayNotification(Context context, String title, String body) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(AppConstant.CHANNEL_ID, AppConstant.CLIENT_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(AppConstant.CHANNEL_DESC);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, AppConstant.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground2)
                .setContentTitle(title)
                .setContentText(body)
                .setColor(context.getResources().getColor(R.color.primary_500))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent resultIntent = new Intent(context, SplashActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SplashActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_MUTABLE);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(context);
        mNotificationMgr.notify(1, mBuilder.build());
    }
}
