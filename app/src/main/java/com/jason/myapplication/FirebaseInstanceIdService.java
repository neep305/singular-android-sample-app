package com.jason.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseInstanceIdService extends FirebaseMessagingService {
    private static final String TAG = "FIREBASE";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.i(TAG, s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "From: " + remoteMessage.getFrom());
            Log.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            String messageBody = remoteMessage.getNotification().getBody();
            String messageTitle = remoteMessage.getNotification().getTitle();

            Log.i(TAG, String.format("messageBody: %s, messageTitle: %s", messageBody, messageTitle));
        }
    }
}
