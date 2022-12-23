package com.bosictsolution.invsale.service;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;

public class MyFirebaseMessagingService extends FirebaseMessagingService{

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        if(message.getNotification()!=null){
            String title=message.getNotification().getTitle();
            String text=message.getNotification().getBody();

            NotificationHelper.displayNotification(getApplicationContext(),title,text);
        }
    }
}
