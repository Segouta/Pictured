package com.example.christian.pictured;

/*
 * By Christian Bijvoets, Minor Programmeren UvA, January 2018.
 * This class handles the events when a notification from Firebase is received.
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessageService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // When a new message has arrived, this method is fired.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        showNotification("A new thing arrived!", "Click here to check it out!");

    }
    void showNotification(String title, String content) {
        // When in the app when a new notification drops, this separate notification will be showed.
        // Sound for notification.
        Uri notificationSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);

        // Vibration pattern.
        long[] array = {3000};

        // Build notification.
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "SnapThatChannel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for SnapThat");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.drawable.new_icon) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(content)// message for notification
                .setVibrate(array)
                .setSound(notificationSound) // set alarm sound for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);

        // Show notification.
        mNotificationManager.notify(0, mBuilder.build());
    }
}