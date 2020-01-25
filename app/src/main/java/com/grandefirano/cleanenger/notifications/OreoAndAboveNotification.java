package com.grandefirano.cleanenger.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.O)
public class OreoAndAboveNotification extends ContextWrapper {

    private static final String ID="channel_cleanenger";
    private static final String NAME="Cleanenger";

    private NotificationManager mNotificationManager;


    public OreoAndAboveNotification(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(ID,NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getNotificationManager().createNotificationChannel(notificationChannel);

    }
    public NotificationManager getNotificationManager(){
        if(mNotificationManager==null){
            mNotificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getONotifications(String title, String body,
                                                  PendingIntent pendingIntent,
                                                  Uri soundUri, String icon){
    return new Notification.Builder(getApplicationContext(),ID)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(body)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setSmallIcon(Integer.parseInt(icon));
    }
}
