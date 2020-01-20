package com.grandefirano.cleanenger.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
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

import androidx.core.app.NotificationCompat;

public class FirebaseService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String tokenRefresh=FirebaseInstanceId.getInstance().getToken();
        if(user!=null){
            updateToken(tokenRefresh);
        }
    }


    //    @Override
//    public void onNewToken(String s) {
//        super.onNewToken(s);
//        Log.d("New_Token",s);
//        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
//        String tokenRefresh= FirebaseInstanceId.getInstance().getToken();
//        if(user!=null){
//            updateToken(tokenRefresh);
//        }
//    }

    private void updateToken(String tokenRefresh) {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("tokens");
        Token token= new Token(tokenRefresh);
        Log.d("sssssssss","tokenrrrrrr");
        reference.child(user.getUid()).setValue(token);
    }



}
