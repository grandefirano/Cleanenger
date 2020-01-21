package com.grandefirano.cleanenger.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.grandefirano.cleanenger.Activities.ChatActivity;
import com.grandefirano.cleanenger.Activities.ShowStoryActivity;

import androidx.core.app.NotificationCompat;

public class FirebaseMessaging extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();

        String sent=remoteMessage.getData().get("sent");
        String user=remoteMessage.getData().get("user");


        if(currentUser!=null
                && sent.equals(currentUser.getUid())
                && !currentUser.getUid().equals(user))
            sendNotification(remoteMessage);



    }
    private void sendNotification(RemoteMessage remoteMessage){
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        String chatId=remoteMessage.getData().get("chatId");

        int i =Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent;
        Bundle bundle = new Bundle();
        if(chatId!=null&&!chatId.equals("")) {
            intent = new Intent(this, ChatActivity.class);
            bundle.putString("id", user);
            bundle.putString("chatId", chatId);
        }else{
            intent=new Intent(this, ShowStoryActivity.class);
            bundle.putString("id",user);
        }
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            sendOAndAboveNotification(icon,body,title,pendingIntent,defSoundUri,i);
        }else{
            sendNormalNotification(icon,body,title,pendingIntent,defSoundUri,i);
        }


    }

    private void sendNormalNotification(String icon,String body,String title,PendingIntent pendingIntent,Uri defSoundUri,int i) {

        NotificationCompat.Builder builder= new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if(i>0){
            j=i;

        }
        notificationManager.notify(j,builder.build());

    }

    private void sendOAndAboveNotification(String icon,String body,String title,PendingIntent pendingIntent,Uri defSoundUri,int i) {

        OreoAndAboveNotification notification1=new OreoAndAboveNotification(this);
        Notification.Builder builder=notification1.getONotifications(title,body,pendingIntent,defSoundUri,icon);

        int j=0;
        if(i>0){
            j=i;

        }
        notification1.getNotificationManager().notify(j,builder.build());

    }
}
