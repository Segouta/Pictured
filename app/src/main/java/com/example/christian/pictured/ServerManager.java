package com.example.christian.pictured;

/*
 * Created by Christian on 17-1-2018.
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.signum;
import static java.lang.Math.toIntExact;

public class ServerManager extends Service implements ChildEventListener {

    private DatabaseReference mDatabase;

    private long endMillis;
    private String thingText;
    private static ServerManager sInstance;

    public ServerManager()
    {
        this.mDatabase =  FirebaseDatabase.getInstance().getReference();;

    }

    public String getThingText() {
        return thingText;
    }

    public long getEndMillis() {
        return endMillis;
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        final ArrayList<String> objects = new ArrayList<>();
        for (DataSnapshot child : dataSnapshot.child("things").getChildren()) {
            String object = child.getValue(String.class);
            objects.add(object);
        }

        Integer thingIndex = dataSnapshot.child("index").getValue(Integer.class);
        Long endMillisTmp = dataSnapshot.child("endMillis").getValue(Long.class);

        if(endMillisTmp != null)
        {
            endMillis = endMillisTmp;
        }

        if (thingIndex != null) {
            thingText = objects.get(thingIndex);
        }

        showNotification("A new present arrived!", "Click here to check it out!");

        PlayActivity.getInstance().newThingArrived(this);

    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        thingText = "No connection to database";

    }

    private static int getRandomNumberInRange(long maxLong) {

        int min = 0;
        int max = (int) maxLong;

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sInstance = this;
        mDatabase.child("currentThing").addChildEventListener(this);

        return START_STICKY;
    }

    void showNotification(String title, String content) {

        Uri notificationSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);

        long[] array = {3000};
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

        mNotificationManager.notify(0, mBuilder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        sInstance = this;
        return null;
    }
}
